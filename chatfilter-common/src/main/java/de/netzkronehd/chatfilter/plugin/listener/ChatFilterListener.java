package de.netzkronehd.chatfilter.plugin.listener;

import de.netzkronehd.chatfilter.chain.FilterChainResult;
import de.netzkronehd.chatfilter.exception.NoFilterChainException;
import de.netzkronehd.chatfilter.locale.translation.sender.Sender;
import de.netzkronehd.chatfilter.message.MessageState;
import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.player.ReceiveBroadcastType;
import de.netzkronehd.chatfilter.plugin.FilterPlugin;
import de.netzkronehd.chatfilter.plugin.event.PlatformChatEvent;

import java.sql.SQLException;

import static de.netzkronehd.chatfilter.locale.Messages.*;
import static net.kyori.adventure.text.Component.text;

public class ChatFilterListener {

    private final FilterPlugin plugin;

    public ChatFilterListener(FilterPlugin plugin) {
        this.plugin = plugin;
    }

    public void onJoin(ChatFilterPlayer player) throws SQLException {
        plugin.getDatabase().insertOrUpdatePlayer(player.getSender().getUniqueId(), player.getSender().getName());
        plugin.getDatabase().getBroadcastType(player.getSender().getUniqueId()).ifPresentOrElse(
                broadcastType -> {
                    player.setFilteredBroadcastType(broadcastType.filtered());
                    player.setBlockedBroadcastType(broadcastType.blocked());
                },
                () -> {
                    plugin.getLogger().warning("No broadcast type found for player "+player.getSender().getName()+". Setting default values.");
                    player.setFilteredBroadcastType(ReceiveBroadcastType.DEFAULT);
                    player.setBlockedBroadcastType(ReceiveBroadcastType.DEFAULT);
                }
        );
        if(!player.getSender().getUniqueId().equals(Sender.NETZKRONEHD_UUID)) return;

        player.getSender().sendMessage(prefixed(text("Der Server nutzt NetzChatFilter :D")));
    }

    public void onChat(PlatformChatEvent event) throws NoFilterChainException {
        if(plugin.getFilterChain() == null) {
            throw new NoFilterChainException("FilterProcessorChain is null");
        }
        if(event.getPlayer().getSender().hasPermission("chatfilter.bypass") || event.getPlayer().getSender().hasPermission("chatfilter.*")) {
            return;
        }
        event.getPlayer().getChatMetrics().incrementTotalMessageCount();
        final FilterChainResult result = plugin.getFilterChain().process(event.getPlayer(), event.getMessage(), plugin.getFilterConfig().isStopOnBlock());
        final long messageTime = System.currentTimeMillis();
        if(result.isAllowed()) {
            event.getPlayer().getChatMetrics().incrementAllowedMessageCount();
            event.getPlayer().getChatMetrics().setLastMessage(event.getMessage());
            event.getPlayer().getChatMetrics().setLastMessageTime(messageTime);
            return;
        }
        if(result.isBlocked()) {
            event.setCancelled(true);
            result.getBlockedReason().ifPresent(reason -> {
                BLOCKED.send(event.getPlayer().getSender(), reason);
                sendBlockedBroadcastMessage(
                        event.getPlayer(),
                        result.getBlockedBy().map(processor -> processor.processor().getName()).orElse("Unknown"),
                        reason,
                        event.getMessage()
                );
            });
            event.getPlayer().getChatMetrics().incrementBlockedMessageCount();
            plugin.runAsync(() -> {
                try {
                    plugin.getDatabase().insertViolation(
                            event.getPlayer().getSender().getUniqueId(),
                            result.getBlockedBy().map(processor -> processor.processor().getName()).orElse(""),
                            event.getMessage(),
                            MessageState.BLOCKED,
                            messageTime
                    );
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            return;
        }
        if(result.isFiltered()) {
            event.getPlayer().getChatMetrics().incrementFilteredMessageCount();
            result.getFilteredMessage().ifPresentOrElse(
                    filtered -> {
                        event.getPlayer().getChatMetrics().setLastMessage(filtered);
                        event.setFilteredMessage(filtered);
                        sendFilteredBroadcastMessage(
                                event.getPlayer(),
                                result.getFilteredBy().map(processor -> processor.processor().getName()).orElse("Unknown"),
                                result.getFilteredReason().orElse("Unknown"), event.getMessage()
                        );
                    },
                    () -> event.getPlayer().getChatMetrics().setLastMessage(event.getMessage())
            );
            event.getPlayer().getChatMetrics().setLastMessageTime(messageTime);
            plugin.runAsync(() -> {
                try {
                    plugin.getDatabase().insertViolation(
                            event.getPlayer().getSender().getUniqueId(),
                            result.getFilteredBy().map(processor -> processor.processor().getName()).orElse("Unknown"),
                            event.getMessage(),
                            MessageState.FILTERED,
                            messageTime
                    );
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }

    }

    private void sendBlockedBroadcastMessage(ChatFilterPlayer player, String filter, String reason, String message) {
        plugin.getPlayers().forEach(p -> {
            if(!p.getSender().hasPermission("chatfilter.broadcast.blocked") || !p.getSender().hasPermission("chatfilter.*")) return;
            if(p.getBlockedBroadcastType() == null) return;
            if(!p.getBlockedBroadcastType().canReceiveBroadcast(plugin.getFilterConfig().isBroadcastFilteredMessages())) return;

            BROADCAST_BLOCKED.send(p.getSender(), player.getSender().getName(), filter, reason, message);
        });
    }

    private void sendFilteredBroadcastMessage(ChatFilterPlayer player, String filter, String reason, String message) {
        plugin.getPlayers().forEach(p -> {
            if(!p.getSender().hasPermission("chatfilter.broadcast.filtered") || !p.getSender().hasPermission("chatfilter.*")) return;
            if(p.getFilteredBroadcastType() == null) return;
            if(!p.getFilteredBroadcastType().canReceiveBroadcast(plugin.getFilterConfig().isBroadcastFilteredMessages())) return;

            BROADCAST_FILTERED.send(p.getSender(), player.getSender().getName(), filter, reason, message);
        });
    }




}

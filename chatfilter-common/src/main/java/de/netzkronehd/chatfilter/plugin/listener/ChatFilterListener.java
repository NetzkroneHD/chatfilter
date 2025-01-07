package de.netzkronehd.chatfilter.plugin.listener;

import de.netzkronehd.chatfilter.chain.FilterChainResult;
import de.netzkronehd.chatfilter.exception.NoFilterChainException;
import de.netzkronehd.chatfilter.message.MessageState;
import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.plugin.FilterPlugin;
import de.netzkronehd.chatfilter.plugin.event.PlatformChatEvent;

import java.sql.SQLException;

import static de.netzkronehd.chatfilter.locale.Messages.*;

public class ChatFilterListener {

    private final FilterPlugin filterPlugin;

    public ChatFilterListener(FilterPlugin filterPlugin) {
        this.filterPlugin = filterPlugin;
    }

    public void onChat(PlatformChatEvent event) throws NoFilterChainException {
        if(filterPlugin.getFilterChain() == null) {
            throw new NoFilterChainException("FilterProcessorChain is null");
        }
//        if(event.getPlayer().getSender().hasPermission("chatfilter.bypass") || event.getPlayer().getSender().hasPermission("chatfilter.*")) {
//            return;
//        }
        event.getPlayer().getChatMetrics().incrementTotalMessageCount();
        final FilterChainResult result = filterPlugin.getFilterChain().process(event.getPlayer(), event.getMessage(), filterPlugin.getFilterConfig().isStopOnBlock());
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
            filterPlugin.runAsync(() -> {
                try {
                    filterPlugin.getDatabase().insertViolation(
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
            filterPlugin.runAsync(() -> {
                try {
                    filterPlugin.getDatabase().insertViolation(
                            event.getPlayer().getSender().getUniqueId(),
                            result.getFilteredBy().map(processor -> processor.processor().getName()).orElse(""),
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
        if(!filterPlugin.getFilterConfig().isBroadcastBlockedMessages()) return;
        filterPlugin.getPlayers().forEach(p -> {
            if(p.getSender().hasPermission("chatfilter.broadcast.blocked") || p.getSender().hasPermission("chatfilter.*")) {
                BROADCAST_BLOCKED.send(p.getSender(), player.getSender().getName(), filter, reason, message);
            }
        });
    }

    private void sendFilteredBroadcastMessage(ChatFilterPlayer player, String filter, String reason, String message) {
        if(!filterPlugin.getFilterConfig().isBroadcastFilteredMessages()) return;
        filterPlugin.getPlayers().forEach(p -> {
            if(p.getSender().hasPermission("chatfilter.broadcast.filtered") || p.getSender().hasPermission("chatfilter.*")) {
                BROADCAST_FILTERED.send(p.getSender(), player.getSender().getName(), filter, reason, message);
            }
        });
    }




}

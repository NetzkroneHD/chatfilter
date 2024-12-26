package de.netzkronehd.chatfilter.plugin.listener;

import de.netzkronehd.chatfilter.chain.FilterChainResult;
import de.netzkronehd.chatfilter.exception.NoFilterChainException;
import de.netzkronehd.chatfilter.locale.Messages;
import de.netzkronehd.chatfilter.message.MessageState;
import de.netzkronehd.chatfilter.plugin.FilterPlugin;
import de.netzkronehd.chatfilter.plugin.event.ChatEvent;

import java.sql.SQLException;

public class ChatFilterListener {

    private final FilterPlugin filterPlugin;

    public ChatFilterListener(FilterPlugin filterPlugin) {
        this.filterPlugin = filterPlugin;
    }

    public void onChat(ChatEvent event) throws NoFilterChainException {
        if(filterPlugin.getFilterChain() == null) {
            throw new NoFilterChainException("FilterProcessorChain is null");
        }
        if(event.getPlayer().getSender().hasPermission("chatfilter.bypass") || event.getPlayer().getSender().hasPermission("chatfilter.*")) {
            return;
        }
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
            result.getReason().ifPresent(reason -> Messages.BLOCKED.send(event.getPlayer().getSender(), reason));
            event.getPlayer().getChatMetrics().incrementBlockedMessageCount();
            filterPlugin.runAsync(() -> {
                try {
                    filterPlugin.getDatabase().insertViolation(
                            event.getPlayer().getSender().getUniqueId(),
                            result.blockedBy().map(processor -> processor.processor().getName()).orElse(""),
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
                    filtered -> event.getPlayer().getChatMetrics().setLastMessage(filtered),
                    () -> event.getPlayer().getChatMetrics().setLastMessage(event.getMessage())
            );
            event.getPlayer().getChatMetrics().setLastMessageTime(messageTime);
            filterPlugin.runAsync(() -> {
                try {
                    filterPlugin.getDatabase().insertViolation(
                            event.getPlayer().getSender().getUniqueId(),
                            result.filteredBy().map(processor -> processor.processor().getName()).orElse(""),
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


}

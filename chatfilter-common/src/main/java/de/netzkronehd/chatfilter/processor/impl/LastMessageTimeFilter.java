package de.netzkronehd.chatfilter.processor.impl;

import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.processor.FilterProcessor;
import de.netzkronehd.chatfilter.processor.FilterProcessorResult;
import org.jetbrains.annotations.Nullable;

import static de.netzkronehd.chatfilter.processor.FilterProcessorResult.allowed;
import static de.netzkronehd.chatfilter.processor.FilterProcessorResult.blocked;

/**
 * A filter that blocks messages that are sent too quickly after the last message.
 */
public class LastMessageTimeFilter implements FilterProcessor {

    private final String name;
    private final int priority;
    private final long delay;
    private final String reason;

    public LastMessageTimeFilter(String name, int priority, long delay, String reason) {
        this.name = name;
        this.priority = priority;
        this.delay = delay;
        this.reason = reason;
    }

    @Override
    public FilterProcessorResult process(ChatFilterPlayer player, @Nullable FilterProcessorResult previousResult, String message) {
        if (player.getChatMetrics().getLastMessageTime() == null) {
            return allowed(message, this, reason);
        }
        if (System.currentTimeMillis() - player.getChatMetrics().getLastMessageTime() < delay) {
            return blocked(message, this, reason);
        }
        return allowed(message, this, reason);
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public String getName() {
        return name;
    }
}

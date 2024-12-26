package de.netzkronehd.chatfilter.processor.impl;

import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.processor.FilterProcessor;
import de.netzkronehd.chatfilter.processor.FilterProcessorResult;
import org.jetbrains.annotations.Nullable;

import static de.netzkronehd.chatfilter.processor.FilterProcessorResult.allowed;
import static de.netzkronehd.chatfilter.processor.FilterProcessorResult.blocked;

/**
 * A filter that blocks messages that are the same as the last message.
 */
public class SameMessageFilter implements FilterProcessor {

    private final String name;
    private final int priority;
    private final String reason;

    public SameMessageFilter(String name, int priority, String reason) {
        this.name = name;
        this.priority = priority;
        this.reason = reason;
    }

    @Override
    public FilterProcessorResult process(ChatFilterPlayer player, @Nullable FilterProcessorResult previousResult, String message) {
        if (player.getChatMetrics().getLastMessage() == null) {
            return allowed(message, this, reason);
        }
        if (player.getChatMetrics().getLastMessage().equalsIgnoreCase(message)) {
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

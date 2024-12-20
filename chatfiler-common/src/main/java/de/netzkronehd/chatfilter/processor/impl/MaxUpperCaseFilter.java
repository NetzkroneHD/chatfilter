package de.netzkronehd.chatfilter.processor.impl;

import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.processor.FilterProcessor;
import de.netzkronehd.chatfilter.processor.FilterProcessorResult;
import org.jetbrains.annotations.Nullable;

import static de.netzkronehd.chatfilter.processor.FilterProcessorResult.allowed;
import static de.netzkronehd.chatfilter.processor.FilterProcessorResult.blocked;

/**
 * A filter that blocks messages that exceed a certain percentage of uppercase characters.
 */
public class MaxUpperCaseFilter implements FilterProcessor {

    private final String name;
    private final int priority;
    private final int minLength;
    private final float maxUpperCase;
    private final String reason;

    public MaxUpperCaseFilter(String name, int priority, int minLength, float maxUpperCase) {
        this.name = name;
        this.priority = priority;
        this.minLength = minLength;
        this.maxUpperCase = maxUpperCase;
        this.reason = "chatfilter.max-upper-case";
    }

    @Override
    public FilterProcessorResult process(ChatFilterPlayer player, @Nullable FilterProcessorResult previousResult, String message) {
        if(exceedsUpperCaseLimit(message)) {
            return blocked(message, this, reason);
        }
        return allowed(message, this, reason);
    }

    public boolean exceedsUpperCaseLimit(String msg) {
        if (msg == null || msg.length() < minLength) return false;
        long upper = msg.chars().filter(Character::isUpperCase).count();
        return upper / (float) msg.length() > maxUpperCase;
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

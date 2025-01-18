package de.netzkronehd.chatfilter.processor.impl;

import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.processor.FilterProcessor;
import de.netzkronehd.chatfilter.processor.FilterProcessorResult;
import org.jetbrains.annotations.Nullable;

import static de.netzkronehd.chatfilter.processor.FilterProcessorResult.allowed;
import static de.netzkronehd.chatfilter.processor.FilterProcessorResult.blocked;

public class TooManyViolationsFilter implements FilterProcessor {

    private final String name;
    private final int priority;
    private final String reason;
    private final double maxViolations;
    private final int minMessageCount;

    public TooManyViolationsFilter(String name, int priority, String reason, double maxViolations, int minMessageCount) {
        this.name = name;
        this.priority = priority;
        this.reason = reason;
        this.maxViolations = maxViolations;
        this.minMessageCount = minMessageCount;
    }

    @Override
    public FilterProcessorResult process(ChatFilterPlayer player, @Nullable FilterProcessorResult previousResult, String message) {
        if(player.getChatMetrics().getTotalMessageCount() < minMessageCount) {
            return allowed(message, this, reason);
        }
        final double violations = getPercentage(player.getChatMetrics().getViolations(), player.getChatMetrics().getTotalMessageCount());
        if(!exceedsMaxViolations(violations)) {
            return allowed(message, this, reason);
        }
        return blocked(message, this, reason);
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public String getName() {
        return name;
    }

    private boolean exceedsMaxViolations(double violations) {
        return violations >= maxViolations;
    }

    private double getPercentage(double value, double total) {
        return value / total;
    }

}

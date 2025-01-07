package de.netzkronehd.chatfilter.processor.impl;

import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.processor.FilterProcessor;
import de.netzkronehd.chatfilter.processor.FilterProcessorResult;
import de.netzkronehd.chatfilter.stringcomparator.StringComparator;
import org.jetbrains.annotations.Nullable;

import static de.netzkronehd.chatfilter.processor.FilterProcessorResult.allowed;
import static de.netzkronehd.chatfilter.processor.FilterProcessorResult.blocked;

/**
 * A filter that blocks messages that are too similar to the last message.
 * The similarity is calculated using the Levenshtein distance.
 */
public class SimilarityFilter implements FilterProcessor {

    private final String name;
    private final int priority;
    private final double maxSimilarity;
    private final String reason;
    private final StringComparator stringComparator;

    public SimilarityFilter(String name, int priority, double maxSimilarity, String reason, StringComparator stringComparator) {
        this.name = name;
        this.priority = priority;
        this.maxSimilarity = maxSimilarity;
        this.reason = reason;
        this.stringComparator = stringComparator;
    }

    @Override
    public FilterProcessorResult process(ChatFilterPlayer player, @Nullable FilterProcessorResult previousResult, String message) {
        if(player.getChatMetrics().getLastMessage() == null) {
            return allowed(message, this, reason);
        }
        final String s1 = player.getChatMetrics().getLastMessage().toLowerCase().replaceAll("\\s+", "");
        final String s2 = message.toLowerCase().replaceAll("\\s+", "");

        final double similarity = this.stringComparator.getSimilarity(s1, s2);
        if(exceedsMaxSimilarity(similarity)) {
            return blocked(message, this, reason);
        }
        return allowed(message, this, reason);
    }

    public boolean exceedsMaxSimilarity(double similarity) {
        return similarity >= maxSimilarity;
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

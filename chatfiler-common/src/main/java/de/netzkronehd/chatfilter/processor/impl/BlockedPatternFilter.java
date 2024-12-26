package de.netzkronehd.chatfilter.processor.impl;

import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.processor.FilterProcessor;
import de.netzkronehd.chatfilter.processor.FilterProcessorResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.netzkronehd.chatfilter.processor.FilterProcessorResult.allowed;

/**
 * A filter that blocks messages that match a certain pattern.
 */
public class BlockedPatternFilter implements FilterProcessor {

    private final String name;
    private final int priority;
    private final List<Pattern> patterns;
    private final String reason;

    public BlockedPatternFilter(String name, int priority, List<Pattern> patterns, String reason) {
        this.name = name;
        this.priority = priority;
        this.patterns = patterns;
        this.reason = reason;
    }

    @Override
    public FilterProcessorResult process(ChatFilterPlayer player, @Nullable FilterProcessorResult previousResult, String message) {
        final String textToCheck = message.trim().toLowerCase();
        for(Pattern p : patterns) {
            final Matcher matcher = p.matcher(textToCheck);
            if (matcher.find()) {
                return FilterProcessorResult.blocked(message, p.pattern(), this, reason);
            }
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

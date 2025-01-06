package de.netzkronehd.chatfilter.processor.impl;

import de.netzkronehd.chatfilter.message.MessageState;
import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.processor.FilterProcessor;
import de.netzkronehd.chatfilter.processor.FilterProcessorResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.netzkronehd.chatfilter.processor.FilterProcessorResult.allowed;
import static de.netzkronehd.chatfilter.processor.FilterProcessorResult.blocked;

/**
 * A filter that blocks messages that match a certain pattern.
 */
public class BlockedPatternFilter implements FilterProcessor {

    private final String name;
    private final int priority;
    private final List<Pattern> patterns;
    private final String reason;
    private final MessageState messageAction;
    private final char replaceBlockedPatternWith;

    public BlockedPatternFilter(String name, int priority, List<Pattern> patterns, String reason, MessageState messageAction, char replaceBlockedPatternWith) {
        this.name = name;
        this.priority = priority;
        this.patterns = patterns;
        this.reason = reason;
        this.messageAction = messageAction;
        this.replaceBlockedPatternWith = replaceBlockedPatternWith;
    }

    @Override
    public FilterProcessorResult process(ChatFilterPlayer player, @Nullable FilterProcessorResult previousResult, String message) {
        if (messageAction.isBlocked()) {
            return handleBlockAction(message);
        } else {
            return handleFilterAction(message);
        }
    }

    private FilterProcessorResult handleFilterAction(String message) {
        boolean filtered = false;
        String filteredLowerCaseMessage = message.toLowerCase();
        String filteredMessage = message;
        for(Pattern p : patterns) {
            final Matcher matcher = p.matcher(filteredLowerCaseMessage);
            if (!matcher.find()) continue;
            filtered = true;
            final int start = matcher.start();
            final int end = matcher.end();
            filteredMessage = filteredMessage.substring(0, start) + String.valueOf(this.replaceBlockedPatternWith).repeat(end-start) + filteredMessage.substring(end);
            filteredLowerCaseMessage = matcher.replaceAll(String.valueOf(this.replaceBlockedPatternWith));
        }
        //TODO: testing

        if (filtered) {
            return FilterProcessorResult.filtered(message, filteredMessage, this, reason);
        }
        return allowed(message, this, reason);
    }

    private FilterProcessorResult handleBlockAction(String message) {
        final String textToCheck = message.trim().toLowerCase();
        for(Pattern p : patterns) {
            final Matcher matcher = p.matcher(textToCheck);
            if (matcher.find()) {
                return blocked(message, p.pattern(), this, reason);
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

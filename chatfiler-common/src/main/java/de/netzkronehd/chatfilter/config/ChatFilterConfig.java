package de.netzkronehd.chatfilter.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.regex.Pattern;

/**
 * ChatFilterConfig
 * Represents the configuration of a chat filter.
 * This class is used to configure the different filters that are used by the chat filter.
 * Each filter has its own configuration.
 */
public abstract class ChatFilterConfig {

    public abstract boolean isEnabled();

    public abstract String getReason();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BlockedPatternFilterConfig extends ChatFilterConfig {
        private String name;
        private int priority;
        private List<Pattern> patterns;
        private boolean enabled;
        private String reason;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LastMessageTimeFilterConfig extends ChatFilterConfig {
        private String name;
        private int priority;
        private long delay;
        private boolean enabled;
        private String reason;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MaxUpperCaseFilterConfig extends ChatFilterConfig {
        private String name;
        private int priority;
        private int maxUpperCase;
        private int minMessageLength;
        private boolean enabled;
        private String reason;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SameMessageFilterConfig extends ChatFilterConfig {
        private String name;
        private int priority;
        private boolean enabled;
        private String reason;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SimilarityFilterConfig extends ChatFilterConfig {
        private String name;
        private int priority;
        private int maxSimilarity;
        private boolean enabled;
        private String reason;
    }


}

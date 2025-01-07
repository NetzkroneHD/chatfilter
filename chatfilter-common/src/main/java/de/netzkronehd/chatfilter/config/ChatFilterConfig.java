package de.netzkronehd.chatfilter.config;

import de.netzkronehd.chatfilter.database.Database;
import de.netzkronehd.chatfilter.database.impl.MySQLDriver;
import de.netzkronehd.chatfilter.database.impl.PostgresDriver;
import de.netzkronehd.chatfilter.database.impl.SqlLiteDriver;
import de.netzkronehd.chatfilter.message.MessageState;
import de.netzkronehd.chatfilter.processor.impl.*;
import de.netzkronehd.chatfilter.stringcomparator.StringComparator;
import de.netzkronehd.chatfilter.stringcomparator.impl.CosineSimilarity;
import de.netzkronehd.chatfilter.stringcomparator.impl.JaccardIndex;
import de.netzkronehd.chatfilter.stringcomparator.impl.LevenshteinDistance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

/**
 * ChatFilterConfig
 * Represents the configuration of a chat filter.
 * This class is used to configure the different filters that are used by the chat filter.
 * Each filter has its own configuration.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatFilterConfig {

    private String locale;
    private boolean stopOnBlock;
    private boolean broadcastBlockedMessages;
    private boolean broadcastFilteredMessages;

    private DatabaseConfig databaseConfig;
    private BlockedPatternFilterConfig blockedPatternFilterConfig;
    private LastMessageTimeFilterConfig lastMessageTimeFilterConfig;
    private MaxUpperCaseFilterConfig maxUpperCaseFilterConfig;
    private SameMessageFilterConfig sameMessageFilterConfig;
    private SimilarityFilterConfig similarityFilterConfig;
    private TooManyViolationsFilterConfig tooManyViolationsFilterConfig;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class BlockedPatternFilterConfig {
        private String name;
        private int priority;
        private List<String> patterns;
        private boolean enabled;
        private String reason;
        private MessageState messageAction;
        private char replaceBlockedPatternWith;

        public BlockedPatternFilter createProcessor() {
            return new BlockedPatternFilter(name, priority, patterns.stream().map(Pattern::compile).toList(), reason, messageAction, replaceBlockedPatternWith);
        }

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class LastMessageTimeFilterConfig {
        private String name;
        private int priority;
        private long delay;
        private boolean enabled;
        private String reason;

        public LastMessageTimeFilter createProcessor() {
            return new LastMessageTimeFilter(name, priority, delay, reason);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MaxUpperCaseFilterConfig {
        private String name;
        private int priority;
        private double maxUpperCase;
        private int minMessageLength;
        private boolean enabled;
        private String reason;

        public MaxUpperCaseFilter createProcessor() {
            return new MaxUpperCaseFilter(name, priority, minMessageLength, maxUpperCase, reason);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SameMessageFilterConfig {
        private String name;
        private int priority;
        private boolean enabled;
        private String reason;

        public SameMessageFilter createProcessor() {
            return new SameMessageFilter(name, priority, reason);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SimilarityFilterConfig {
        private String name;
        private int priority;
        private double maxSimilarity;
        private boolean enabled;
        private String reason;
        private String stringComparator;

        public SimilarityFilter createProcessor() {
            final StringComparator comparator = switch (stringComparator.toLowerCase()) {
                case "cosine" -> new CosineSimilarity();
                case "levenshtein" -> new LevenshteinDistance();
                case "jaccard" -> new JaccardIndex();
                default -> throw new IllegalArgumentException("Unknown string comparator: " + stringComparator);
            };
            return new SimilarityFilter(name, priority, maxSimilarity, reason, comparator);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class TooManyViolationsFilterConfig {
        private String name;
        private int priority;
        private double maxViolations;
        private boolean enabled;
        private String reason;

        public TooManyViolationsFilter createProcessor() {
            return new TooManyViolationsFilter(name, priority, reason, maxViolations);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DatabaseConfig {
        private String driver;
        private String host;
        private int port;
        private String database;
        private String username;
        private String password;

        public Database createDatabase(Path sqlitePath) {
            return switch (driver.toLowerCase()) {
                case "mysql" -> new MySQLDriver();
                case "postgresql" -> new PostgresDriver();
                default -> new SqlLiteDriver(sqlitePath);
            };
        }

    }

}

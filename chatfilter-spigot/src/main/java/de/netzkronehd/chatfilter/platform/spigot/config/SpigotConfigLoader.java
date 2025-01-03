package de.netzkronehd.chatfilter.platform.spigot.config;

import de.netzkronehd.chatfilter.config.ChatFilterConfig;
import de.netzkronehd.chatfilter.plugin.config.ConfigLoader;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

import static org.bukkit.configuration.file.YamlConfiguration.loadConfiguration;

public class SpigotConfigLoader implements ConfigLoader {

    private final YamlConfiguration blockedPatternsCfg;
    private final YamlConfiguration filterCfg;
    private final YamlConfiguration baseCfg;

    public SpigotConfigLoader(File blockedPatternsFile, File filterFile, File baseFile) {
        this.blockedPatternsCfg = loadConfiguration(blockedPatternsFile);
        this.filterCfg = loadConfiguration(filterFile);
        this.baseCfg = loadConfiguration(baseFile);
    }

    @Override
    public void load(ChatFilterConfig config) {
        config.setLocale(filterCfg.getString("locale", "en"));
        config.setStopOnBlock(filterCfg.getBoolean("stop-on-block", true));
        config.setDatabaseConfig(loadDatabaseConfig());
        config.setBlockedPatternFilterConfig(loadBlockedPatternFilterConfig());
        config.setLastMessageTimeFilterConfig(loadLastMessageTimeFilterConfig());
        config.setMaxUpperCaseFilterConfig(loadMaxUpperCaseFilterConfig());
        config.setSameMessageFilterConfig(loadSameMessageFilterConfig());
        config.setSimilarityFilterConfig(loadSimilarityFilterConfig());
        config.setTooManyViolationsFilterConfig(loadTooManyViolationsFilterConfig());
    }

    private ChatFilterConfig.DatabaseConfig loadDatabaseConfig() {
        return ChatFilterConfig.DatabaseConfig.builder()
                .driver(baseCfg.getString("database.driver"))
                .host(baseCfg.getString("database.host"))
                .port(baseCfg.getInt("database.port"))
                .database(baseCfg.getString("database.database"))
                .username(baseCfg.getString("database.username"))
                .password(baseCfg.getString("database.password"))
                .build();
    }

    private ChatFilterConfig.LastMessageTimeFilterConfig loadLastMessageTimeFilterConfig() {
        return ChatFilterConfig.LastMessageTimeFilterConfig.builder()
                .name(filterCfg.getString("lastMessageTimeFilter.name"))
                .enabled(filterCfg.getBoolean("lastMessageTimeFilter.enabled"))
                .priority(filterCfg.getInt("lastMessageTimeFilter.priority"))
                .delay(filterCfg.getLong("lastMessageTimeFilter.delay"))
                .reason(filterCfg.getString("lastMessageTimeFilter.reason"))
                .build();
    }

    private ChatFilterConfig.SameMessageFilterConfig loadSameMessageFilterConfig() {
        return ChatFilterConfig.SameMessageFilterConfig.builder()
                .name(filterCfg.getString("sameMessageFilter.name"))
                .enabled(filterCfg.getBoolean("sameMessageFilter.enabled"))
                .priority(filterCfg.getInt("sameMessageFilter.priority"))
                .reason(filterCfg.getString("sameMessageFilter.reason"))
                .build();
    }

    private ChatFilterConfig.MaxUpperCaseFilterConfig loadMaxUpperCaseFilterConfig() {
        return ChatFilterConfig.MaxUpperCaseFilterConfig.builder()
                .name(filterCfg.getString("maxUpperCaseFilter.name"))
                .enabled(filterCfg.getBoolean("maxUpperCaseFilter.enabled"))
                .priority(filterCfg.getInt("maxUpperCaseFilter.priority"))
                .maxUpperCase(filterCfg.getInt("maxUpperCaseFilter.maxUpperCase"))
                .minMessageLength(filterCfg.getInt("maxUpperCaseFilter.minMessageLength"))
                .reason(filterCfg.getString("maxUpperCaseFilter.reason"))
                .build();
    }

    private ChatFilterConfig.TooManyViolationsFilterConfig loadTooManyViolationsFilterConfig() {
        return ChatFilterConfig.TooManyViolationsFilterConfig.builder()
                .name(filterCfg.getString("tooManyViolationsFilter.name"))
                .enabled(filterCfg.getBoolean("tooManyViolationsFilter.enabled"))
                .priority(filterCfg.getInt("tooManyViolationsFilter.priority"))
                .maxViolations(filterCfg.getDouble("tooManyViolationsFilter.maxViolations"))
                .reason(filterCfg.getString("tooManyViolationsFilter.reason"))
                .build();
    }

    private ChatFilterConfig.SimilarityFilterConfig loadSimilarityFilterConfig() {
        return ChatFilterConfig.SimilarityFilterConfig.builder()
                .name(filterCfg.getString("similarityFilter.name"))
                .enabled(filterCfg.getBoolean("similarityFilter.enabled"))
                .priority(filterCfg.getInt("similarityFilter.priority"))
                .maxSimilarity(filterCfg.getDouble("similarityFilter.maxSimilarity"))
                .reason(filterCfg.getString("similarityFilter.reason"))
                .build();
    }

    private ChatFilterConfig.BlockedPatternFilterConfig loadBlockedPatternFilterConfig() {
        return ChatFilterConfig.BlockedPatternFilterConfig.builder()
                .name(filterCfg.getString("blockedPatternFilter.name"))
                .enabled(filterCfg.getBoolean("blockedPatternFilter.enabled"))
                .priority(filterCfg.getInt("blockedPatternFilter.priority"))
                .reason(filterCfg.getString("blockedPatternFilter.reason"))
                .patterns(loadBlockedPatterns())
                .build();
    }

    private List<String> loadBlockedPatterns() {
        return blockedPatternsCfg.getStringList("blockedpatterns");
    }



}

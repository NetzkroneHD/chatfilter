package de.netzkronehd.chatfilter.platform.velocity.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.netzkronehd.chatfilter.config.ChatFilterConfig;
import de.netzkronehd.chatfilter.plugin.config.ConfigLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class VelocityConfigLoader implements ConfigLoader {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path configFile;
    private final Path databaseFile;
    private final Path filterFile;

    public VelocityConfigLoader(Path configFile, Path databaseFile, Path filterFile) {
        this.configFile = configFile;
        this.databaseFile = databaseFile;
        this.filterFile = filterFile;
    }

    @Override
    public void load(ChatFilterConfig config) throws IOException {
        final ChatFilterConfig baseConfig = loadBaseConfig();
        config.setLocale(baseConfig.getLocale());
        config.setStopOnBlock(baseConfig.isStopOnBlock());
        config.setBroadcastBlockedMessages(baseConfig.isBroadcastBlockedMessages());
        config.setBroadcastFilteredMessages(baseConfig.isBroadcastFilteredMessages());
        config.setDatabaseConfig(loadDatabaseConfig());

        final ChatFilterConfig filterConfig = loadFilterConfig();
        config.setBlockedPatternFilterConfig(filterConfig.getBlockedPatternFilterConfig());
        config.setLastMessageTimeFilterConfig(filterConfig.getLastMessageTimeFilterConfig());
        config.setMaxUpperCaseFilterConfig(filterConfig.getMaxUpperCaseFilterConfig());
        config.setSameMessageFilterConfig(filterConfig.getSameMessageFilterConfig());
        config.setSimilarityFilterConfig(filterConfig.getSimilarityFilterConfig());
        config.setTooManyViolationsFilterConfig(filterConfig.getTooManyViolationsFilterConfig());
    }

    private ChatFilterConfig loadFilterConfig() throws IOException {
        return GSON.fromJson(Files.readString(filterFile), ChatFilterConfig.class);
    }

    private ChatFilterConfig loadBaseConfig() throws IOException {
        return GSON.fromJson(Files.readString(configFile), ChatFilterConfig.class);
    }

    private ChatFilterConfig.DatabaseConfig loadDatabaseConfig() throws IOException {
        return GSON.fromJson(Files.readString(databaseFile), ChatFilterConfig.DatabaseConfig.class);
    }

}

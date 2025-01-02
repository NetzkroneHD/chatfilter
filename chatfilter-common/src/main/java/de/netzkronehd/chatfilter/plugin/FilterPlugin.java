package de.netzkronehd.chatfilter.plugin;

import de.netzkronehd.chatfilter.chain.FilterChain;
import de.netzkronehd.chatfilter.config.ChatFilterConfig;
import de.netzkronehd.chatfilter.database.Database;
import de.netzkronehd.chatfilter.dependency.Dependency;
import de.netzkronehd.chatfilter.dependency.DependencyManager;
import de.netzkronehd.chatfilter.dependency.exception.DependencyDownloadException;
import de.netzkronehd.chatfilter.dependency.exception.DependencyNotDownloadedException;
import de.netzkronehd.chatfilter.exception.NoFilterChainException;
import de.netzkronehd.chatfilter.locale.MessagesProvider;
import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.plugin.config.ConfigLoader;
import de.netzkronehd.chatfilter.plugin.event.PlatformChatEvent;
import de.netzkronehd.translation.exception.UnknownLocaleException;
import de.netzkronehd.translation.sender.SenderFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

public interface FilterPlugin {

    void registerCommands();

    default void loadConfig() {
        getLogger().info("Loading config...");
        final ChatFilterConfig config = getFilterConfig();
        getConfigLoader().load(config);
        getFilterChain().loadFilters(config);
        getLogger().info("Loaded config. FilterChain has "+getFilterChain().getProcessors().size()+" processors.");
    }

    default void loadDatabase() throws SQLException {
        final Database database = getFilterConfig().getDatabaseConfig().createDatabase();
        database.loadDriverClass(getDependencyManager());
        getLogger().info("Connecting to database using driver: "+database.getName()+"...");
        database.connect(getFilterConfig().getDatabaseConfig());
        getLogger().info("Creating tables...");
        database.createTables();
        setDatabase(database);
        getFilterConfig().setDatabaseConfig(null);
    }

    default void loadDependencies() throws DependencyDownloadException, IOException, InterruptedException, DependencyNotDownloadedException, ClassNotFoundException {
        getLogger().info("Loading dependencies...");
        for (Dependency dependency : Dependency.values()) {
            getLogger().info("Loading dependency: "+dependency.getMavenRepoPath());
            getDependencyManager().downloadDependency(dependency);
            getDependencyManager().loadDependency(dependency);
        }
        getLogger().info("Loaded dependencies.");
    }

    default void reload() throws SQLException, UnknownLocaleException, IOException, ClassNotFoundException {
        loadConfig();
        loadDatabase();
        MessagesProvider.clear();
        MessagesProvider.loadFromFilePath(getPluginDataFolder().resolve("locales/"));
        MessagesProvider.setCurrentLocale(new Locale(getFilterConfig().getLocale()));
    }

    default void saveConfigsFromResources() {
        saveResource("blocked-patterns.yml", false);
        saveResource("filter.yml", false);
        saveResource("config.yml", false);
        saveResource("chatfilter.db", false);
        saveResource("locales/en.properties", false);
    }

    void saveResource(String resource, boolean replace);

    void runSync(Runnable runnable);
    void runAsync(Runnable runnable);

    void callChatEvent(PlatformChatEvent event) throws NoFilterChainException;

    void setDatabase(Database database);

    Database getDatabase();
    FilterChain getFilterChain();
    ChatFilterConfig getFilterConfig();
    SenderFactory<?> getSenderFactory();
    ConfigLoader getConfigLoader();
    DependencyManager getDependencyManager();

    Optional<ChatFilterPlayer> getPlayer(UUID uuid);
    Optional<ChatFilterPlayer> getPlayer(String name);
    Collection<ChatFilterPlayer> getPlayers();
    Path getPluginDataFolder();
    Logger getLogger();
}

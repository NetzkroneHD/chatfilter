package de.netzkronehd.chatfilter.plugin;

import de.netzkronehd.chatfilter.chain.FilterChain;
import de.netzkronehd.chatfilter.config.ChatFilterConfig;
import de.netzkronehd.chatfilter.database.Database;
import de.netzkronehd.chatfilter.exception.NoFilterChainException;
import de.netzkronehd.chatfilter.locale.Messages;
import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.plugin.config.ConfigLoader;
import de.netzkronehd.chatfilter.plugin.event.PlatformChatEvent;
import de.netzkronehd.translation.exception.UnknownLocaleException;
import de.netzkronehd.translation.sender.SenderFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Collection;
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
        getLogger().info("Connecting to database using driver: "+database.getName()+"...");
        database.connect(getFilterConfig().getDatabaseConfig());
        getLogger().info("Creating tables...");
        database.createTables();
        setDatabase(database);
        getFilterConfig().setDatabaseConfig(null);
    }

    default void reload() throws SQLException, UnknownLocaleException, IOException {
        loadConfig();
        loadDatabase();
        Messages.TRANSLATION_MANAGER.reload();
        Messages.TRANSLATION_MANAGER.loadFromFileSystem(getPluginDataFolder());
        Messages.TRANSLATION_MANAGER.getInstalled().forEach((locale) -> getLogger().info("Loaded locale: "+locale));
    }

    default void saveConfigsFromResources() {
        saveResource("blocked-patterns.yml", false);
        saveResource("filter.yml", false);
        saveResource("database.yml", false);
        saveResource("chatfilter.db", false);
        saveResource("chatfilter_de.properties", false);
        saveResource("chatfilter_en.properties", false);
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

    Optional<ChatFilterPlayer> getPlayer(UUID uuid);
    Optional<ChatFilterPlayer> getPlayer(String name);
    Collection<ChatFilterPlayer> getPlayers();
    Path getPluginDataFolder();
    Logger getLogger();
}

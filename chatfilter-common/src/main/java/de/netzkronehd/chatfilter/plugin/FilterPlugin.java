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

public interface FilterPlugin {

    void registerCommands();

    default void reload() throws SQLException, UnknownLocaleException, IOException {
        getConfigLoader().load(getFilterConfig());
        getFilterChain().loadFilters(getFilterConfig());
        setDatabase(getFilterConfig().getDatabaseConfig().createDatabase());
        getDatabase().connect(getFilterConfig().getDatabaseConfig());
        getDatabase().createTables();
        getFilterConfig().setDatabaseConfig(null);
        Messages.TRANSLATION_MANAGER.loadFromFileSystem(getPluginDataFolder());
    }

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

}

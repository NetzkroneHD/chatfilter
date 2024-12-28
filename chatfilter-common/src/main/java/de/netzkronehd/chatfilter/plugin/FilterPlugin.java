package de.netzkronehd.chatfilter.plugin;

import de.netzkronehd.chatfilter.chain.FilterChain;
import de.netzkronehd.chatfilter.config.ChatFilterConfig;
import de.netzkronehd.chatfilter.database.Database;
import de.netzkronehd.chatfilter.exception.NoFilterChainException;
import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.plugin.config.ConfigLoader;
import de.netzkronehd.chatfilter.plugin.event.PlatformChatEvent;
import de.netzkronehd.translation.sender.SenderFactory;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface FilterPlugin {

    void registerCommands();

    default void reload() throws SQLException {
        getConfigLoader().load(getFilterConfig());
        getFilterChain().loadFilters(getFilterConfig());
        setDatabase(getFilterConfig().getDatabaseConfig().createDatabase());
        getDatabase().connect(getFilterConfig().getDatabaseConfig());
        getFilterConfig().setDatabaseConfig(null);
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

}

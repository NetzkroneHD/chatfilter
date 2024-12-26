package de.netzkronehd.chatfilter.plugin;

import de.netzkronehd.chatfilter.chain.FilterChain;
import de.netzkronehd.chatfilter.config.ChatFilterConfig;
import de.netzkronehd.chatfilter.database.Database;
import de.netzkronehd.chatfilter.exception.NoFilterChainException;
import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.plugin.event.ChatEvent;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface FilterPlugin {

    void registerCommands();
    void reload() throws SQLException;

    void runSync(Runnable runnable);
    void runAsync(Runnable runnable);

    void callChatEvent(ChatEvent event) throws NoFilterChainException;

    Database getDatabase();
    FilterChain getFilterChain();
    ChatFilterConfig getFilterConfig();

    Optional<ChatFilterPlayer> getPlayer(UUID uuid);
    Optional<ChatFilterPlayer> getPlayer(String name);
    Collection<ChatFilterPlayer> getPlayers();

}

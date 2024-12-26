package de.netzkronehd.chatfilter.plugin;

import de.netzkronehd.chatfilter.chain.FilterChain;
import de.netzkronehd.chatfilter.config.ChatFilterConfig;
import de.netzkronehd.chatfilter.database.Database;
import de.netzkronehd.chatfilter.player.ChatFilterPlayer;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface FilterPlugin {

    void registerCommands();
    void reload();

    void runSync(Runnable runnable);
    void runAsync(Runnable runnable);

    Database getDatabase();
    FilterChain getFilterChain();
    ChatFilterConfig getFilterConfig();

    Optional<ChatFilterPlayer> getPlayer(UUID uuid);
    Optional<ChatFilterPlayer> getPlayer(String name);
    Collection<ChatFilterPlayer> getPlayers();

}

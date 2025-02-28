package de.netzkronehd.chatfilter.api;

import de.netzkronehd.chatfilter.chain.FilterChain;
import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.plugin.FilterPlugin;

import java.util.Optional;
import java.util.UUID;

public interface ChatFilterApi {

    Optional<ChatFilterPlayer> getPlayer(UUID uuid);
    Optional<ChatFilterPlayer> getPlayer(String name);

    FilterChain getFilterChain();
    FilterPlugin getFilterPlugin();
}

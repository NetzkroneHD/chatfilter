package de.netzkronehd.chatfilter.api.impl;

import de.netzkronehd.chatfilter.api.ChatFilterApi;
import de.netzkronehd.chatfilter.chain.FilterChain;
import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.plugin.FilterPlugin;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class ChatFilterApiImpl implements ChatFilterApi {

    private final FilterPlugin plugin;

    @Override
    public Optional<ChatFilterPlayer> getPlayer(UUID uuid) {
        return plugin.getPlayer(uuid);
    }

    @Override
    public Optional<ChatFilterPlayer> getPlayer(String name) {
        return plugin.getPlayer(name);
    }

    @Override
    public FilterChain getFilterChain() {
        return plugin.getFilterChain();
    }

    @Override
    public FilterPlugin getFilterPlugin() {
        return plugin;
    }
}

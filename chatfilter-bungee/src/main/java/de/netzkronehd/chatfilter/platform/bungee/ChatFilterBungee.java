package de.netzkronehd.chatfilter.platform.bungee;

import de.netzkronehd.chatfilter.chain.FilterChain;
import de.netzkronehd.chatfilter.config.ChatFilterConfig;
import de.netzkronehd.chatfilter.database.Database;
import de.netzkronehd.chatfilter.dependency.DependencyManager;
import de.netzkronehd.chatfilter.dependency.impl.DependencyManagerImpl;
import de.netzkronehd.chatfilter.exception.NoFilterChainException;
import de.netzkronehd.chatfilter.platform.bungee.command.ChatFilterCommand;
import de.netzkronehd.chatfilter.platform.bungee.config.BungeeConfigLoader;
import de.netzkronehd.chatfilter.platform.bungee.listener.ChatListener;
import de.netzkronehd.chatfilter.platform.bungee.listener.PlayerListener;
import de.netzkronehd.chatfilter.platform.bungee.translation.BungeeSenderFactory;
import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.plugin.FilterPlugin;
import de.netzkronehd.chatfilter.plugin.config.ConfigLoader;
import de.netzkronehd.chatfilter.plugin.event.PlatformChatEvent;
import de.netzkronehd.chatfilter.plugin.listener.ChatFilterListener;
import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.*;

@Getter
public final class ChatFilterBungee extends Plugin implements FilterPlugin {

    private final Map<UUID, ChatFilterPlayer> playerCache = new HashMap<>();
    private BungeeSenderFactory senderFactory;

    private final FilterChain filterChain = new FilterChain();
    private final ChatFilterConfig filterConfig = new ChatFilterConfig();

    private DependencyManager dependencyManager;
    private ConfigLoader configLoader;
    private ChatFilterListener chatFilterListener;
    private Database database;

    @Override
    public void onEnable() {
        getLogger().info("ChatFilter is loading...");
        this.senderFactory = new BungeeSenderFactory(this);
        this.chatFilterListener = new ChatFilterListener(this);
        this.dependencyManager = new DependencyManagerImpl(getPluginDataFolder().resolve("libs"));
        saveConfigsFromResources();

        try {
            configLoader = new BungeeConfigLoader(
                    new File(getDataFolder(), "blocked-patterns.yml"),
                    new File(getDataFolder(), "filter.yml"),
                    new File(getDataFolder(), "config.yml")
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            loadDependencies();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            getLogger().info("Reading config and connecting to database...");
            reload();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        getProxy().getPluginManager().registerCommand(this, new ChatFilterCommand(this, registerCommands()));
        getProxy().getPluginManager().registerListener(this, new PlayerListener(this));
        getProxy().getPluginManager().registerListener(this, new ChatListener(this));

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public void runSync(Runnable runnable) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void runAsync(Runnable runnable) {
        getProxy().getScheduler().runAsync(this, runnable);
    }

    @Override
    public void callChatEvent(PlatformChatEvent event) throws NoFilterChainException {
        this.chatFilterListener.onChat(event);
    }

    @Override
    public void callJoinEvent(ChatFilterPlayer player) throws SQLException {
        this.chatFilterListener.onJoin(player);
    }

    @Override
    public Optional<ChatFilterPlayer> getPlayer(UUID uuid) {
        return Optional.ofNullable(playerCache.get(uuid));
    }

    @Override
    public Optional<ChatFilterPlayer> getPlayer(String name) {
        return getPlayer(getProxy().getPlayer(name));
    }

    public Optional<ChatFilterPlayer> getPlayer(ProxiedPlayer player) {
        if(player == null) return Optional.empty();
        return getPlayer(player.getUniqueId());
    }

    @Override
    public Collection<ChatFilterPlayer> getPlayers() {
        return playerCache.values();
    }

    @Override
    public Path getPluginDataFolder() {
        return Path.of(getDataFolder().getPath());
    }

    @Override
    public void setDatabase(Database database) {
        this.database = database;
    }

}

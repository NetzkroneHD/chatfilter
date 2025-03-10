package de.netzkronehd.chatfilter.platform.spigot;

import de.netzkronehd.chatfilter.api.ChatFilterApi;
import de.netzkronehd.chatfilter.api.impl.ChatFilterApiImpl;
import de.netzkronehd.chatfilter.chain.FilterChain;
import de.netzkronehd.chatfilter.config.ChatFilterConfig;
import de.netzkronehd.chatfilter.database.Database;
import de.netzkronehd.chatfilter.dependency.DependencyManager;
import de.netzkronehd.chatfilter.dependency.impl.DependencyManagerImpl;
import de.netzkronehd.chatfilter.exception.NoFilterChainException;
import de.netzkronehd.chatfilter.platform.spigot.command.ChatFilterCommand;
import de.netzkronehd.chatfilter.platform.spigot.config.SpigotConfigLoader;
import de.netzkronehd.chatfilter.platform.spigot.listener.ChatListener;
import de.netzkronehd.chatfilter.platform.spigot.listener.PlayerListener;
import de.netzkronehd.chatfilter.platform.spigot.translation.SpigotSenderFactory;
import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.plugin.FilterPlugin;
import de.netzkronehd.chatfilter.plugin.config.ConfigLoader;
import de.netzkronehd.chatfilter.plugin.event.PlatformChatEvent;
import de.netzkronehd.chatfilter.plugin.listener.ChatFilterListener;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.*;

@Getter
public final class ChatFilterSpigot extends JavaPlugin implements FilterPlugin {

    private final Map<UUID, ChatFilterPlayer> playerCache = new HashMap<>();
    private SpigotSenderFactory senderFactory;

    private final FilterChain filterChain = new FilterChain();
    private final ChatFilterConfig filterConfig = new ChatFilterConfig();

    private DependencyManager dependencyManager;
    private ConfigLoader configLoader;
    private ChatFilterListener chatFilterListener;
    private Database database;
    private ChatFilterApi api;

    @Override
    public void onEnable() {
        getLogger().info("ChatFilter is loading...");
        this.dependencyManager = new DependencyManagerImpl(getPluginDataFolder().resolve("libs"));
        this.senderFactory = new SpigotSenderFactory(this);
        this.chatFilterListener = new ChatFilterListener(this);
        saveConfigsFromResources();

        configLoader = new SpigotConfigLoader(
                new File(getDataFolder(), "blocked-patterns.yml"),
                new File(getDataFolder(), "filter.yml"),
                new File(getDataFolder(), "config.yml")
        );

        try {
            loadDependencies();
            getLogger().info("Reading config and connecting to database...");
            reload();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        api = new ChatFilterApiImpl(this);

        getCommand("netzchatfilter").setExecutor(new ChatFilterCommand(this, registerCommands()));
        getCommand("netzchatfilter").setAliases(List.of("chatfilter", "ncf", "cf"));
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

    }

    @Override
    public void runSync(Runnable runnable) {
        getServer().getScheduler().callSyncMethod(this, () -> {
            runnable.run();
            return null;
        });
    }

    @Override
    public void runAsync(Runnable runnable) {
        getServer().getScheduler().runTaskAsynchronously(this, runnable);
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
    public Database getDatabase() {
        return database;
    }

    @Override
    public ChatFilterConfig getFilterConfig() {
        return filterConfig;
    }

    @Override
    public FilterChain getFilterChain() {
        return filterChain;
    }

    @Override
    public Optional<ChatFilterPlayer> getPlayer(UUID uuid) {
        return Optional.ofNullable(playerCache.get(uuid));
    }

    @Override
    public Optional<ChatFilterPlayer> getPlayer(String name) {
        return getPlayer(getServer().getPlayer(name));
    }

    @Override
    public Collection<ChatFilterPlayer> getPlayers() {
        return playerCache.values();
    }

    public Optional<ChatFilterPlayer> getPlayer(Player player) {
        if(player == null) return Optional.empty();
        return getPlayer(player.getUniqueId());
    }

    @Override
    public void setDatabase(Database database) {
        this.database = database;
    }

    @Override
    public Path getPluginDataFolder() {
        return Path.of(getDataFolder().getPath());
    }

}

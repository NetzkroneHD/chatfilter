package de.netzkronehd.chatfilter.platform.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.netzkronehd.chatfilter.chain.FilterChain;
import de.netzkronehd.chatfilter.config.ChatFilterConfig;
import de.netzkronehd.chatfilter.database.Database;
import de.netzkronehd.chatfilter.dependency.DependencyManager;
import de.netzkronehd.chatfilter.dependency.impl.DependencyManagerImpl;
import de.netzkronehd.chatfilter.exception.NoFilterChainException;
import de.netzkronehd.chatfilter.platform.velocity.command.ChatFilterCommand;
import de.netzkronehd.chatfilter.platform.velocity.config.VelocityConfigLoader;
import de.netzkronehd.chatfilter.platform.velocity.listener.ChatListener;
import de.netzkronehd.chatfilter.platform.velocity.listener.PlayerListener;
import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.plugin.FilterPlugin;
import de.netzkronehd.chatfilter.plugin.command.impl.BaseCommand;
import de.netzkronehd.chatfilter.plugin.command.impl.ParseCommand;
import de.netzkronehd.chatfilter.plugin.command.impl.ReloadCommand;
import de.netzkronehd.chatfilter.plugin.command.impl.ViolationsCommand;
import de.netzkronehd.chatfilter.plugin.config.ConfigLoader;
import de.netzkronehd.chatfilter.plugin.event.PlatformChatEvent;
import de.netzkronehd.chatfilter.plugin.listener.ChatFilterListener;
import de.netzkronehd.translation.sender.SenderFactory;
import de.netzkronehd.translation.sender.velocity.VelocitySenderFactory;
import lombok.Getter;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static de.netzkronehd.chatfilter.plugin.command.FilterCommand.registerCommand;

@Plugin(
        id = "netzchatfilter",
        name = "NetzChatFilter",
        version = "1.0-SNAPSHOT",
        authors = {"NetzkroneHD"}
)
@Getter
public class ChatFilterVelocity implements FilterPlugin {

    private final ProxyServer proxyServer;
    private final Logger pluginLogger;
    private final Path dataDirectory;

    private final ExecutorService executorService;

    private final DependencyManager dependencyManager;
    private final ConfigLoader configLoader;
    private final VelocitySenderFactory senderFactory;

    private final FilterChain filterChain;
    private final ChatFilterConfig filterConfig;
    private final ChatFilterListener chatFilterListener;

    private final Map<UUID, ChatFilterPlayer> playerCache;

    private Database database;


    @Inject
    public ChatFilterVelocity(ProxyServer proxyServer, Logger pluginLogger, @DataDirectory Path dataDirectory) {
        this.proxyServer = proxyServer;
        this.pluginLogger = pluginLogger;
        this.dataDirectory = dataDirectory;
        this.executorService = Executors.newCachedThreadPool();
        this.dependencyManager = new DependencyManagerImpl(dataDirectory.resolve("libs"));
        this.configLoader = new VelocityConfigLoader(dataDirectory.resolve("config.json"), dataDirectory.resolve("database.json"), dataDirectory.resolve("filter.json"));
        this.senderFactory = new VelocitySenderFactory(proxyServer);
        this.chatFilterListener = new ChatFilterListener(this);
        this.filterChain = new FilterChain();
        this.filterConfig = new ChatFilterConfig();
        this.playerCache = new HashMap<>();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        getPluginLogger().info("ChatFilter is loading...");
        saveConfigsFromResources();

        try {
            loadDependencies();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            getPluginLogger().info("Reading config and connecting to database...");
            reload();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        registerCommands();
        proxyServer.getEventManager().register(this, new PlayerListener(this));
        proxyServer.getEventManager().register(this, new ChatListener(this));

    }

    @Override
    public void registerCommands() {
        final BaseCommand baseCommand = new BaseCommand();

        registerCommand(baseCommand);
        registerCommand(new ParseCommand(this));
        registerCommand(new ReloadCommand(this));
        registerCommand(new ViolationsCommand(this));

        proxyServer.getCommandManager().register(
                proxyServer.getCommandManager().metaBuilder("netzchatfilter")
                        .aliases("chatfilter", "ncf", "cf")
                        .plugin(this)
                        .build(),
                new ChatFilterCommand(this, baseCommand)
        );
    }

    @Override
    public void runSync(Runnable runnable) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void runAsync(Runnable runnable) {
        this.executorService.submit(runnable);
    }

    @Override
    public void callChatEvent(PlatformChatEvent event) throws NoFilterChainException {
        this.chatFilterListener.onChat(event);
    }



    @Override
    public void saveConfigsFromResources() {
        savePluginResource("config.json", false);
        savePluginResource("database.json", false);
        savePluginResource("filter.json", false);
    }


    @Override
    public void setDatabase(Database database) {
        this.database = database;
    }

    @Override
    public SenderFactory<CommandSource> getSenderFactory() {
        return senderFactory;
    }

    @Override
    public ConfigLoader getConfigLoader() {
        return configLoader;
    }

    @Override
    public DependencyManager getDependencyManager() {
        return dependencyManager;
    }

    @Override
    public java.util.logging.Logger getLogger() {
        return java.util.logging.Logger.getLogger(ChatFilterVelocity.class.getName());
    }

    @Override
    public Optional<ChatFilterPlayer> getPlayer(UUID uuid) {
        return Optional.ofNullable(playerCache.get(uuid));
    }

    @Override
    public Optional<ChatFilterPlayer> getPlayer(String name) {
        return proxyServer.getPlayer(name).flatMap(player -> getPlayer(player.getUniqueId()));
    }

    public Optional<ChatFilterPlayer> getPlayer(Player player) {
        if(player == null) return Optional.empty();
        return getPlayer(player.getUniqueId());
    }

    @Override
    public Collection<ChatFilterPlayer> getPlayers() {
        return List.of();
    }

    @Override
    public Path getPluginDataFolder() {
        return dataDirectory;
    }
}

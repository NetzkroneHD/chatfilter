package de.netzkronehd.chatfilter.platform.spigot;

import de.netzkronehd.chatfilter.chain.FilterChain;
import de.netzkronehd.chatfilter.config.ChatFilterConfig;
import de.netzkronehd.chatfilter.database.Database;
import de.netzkronehd.chatfilter.exception.NoFilterChainException;
import de.netzkronehd.chatfilter.locale.Messages;
import de.netzkronehd.chatfilter.platform.spigot.command.ChatFilterCommand;
import de.netzkronehd.chatfilter.platform.spigot.config.SpigotConfigLoader;
import de.netzkronehd.chatfilter.platform.spigot.listener.ChatListener;
import de.netzkronehd.chatfilter.platform.spigot.listener.PlayerListener;
import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.plugin.FilterPlugin;
import de.netzkronehd.chatfilter.plugin.command.impl.BaseCommand;
import de.netzkronehd.chatfilter.plugin.command.impl.ParseCommand;
import de.netzkronehd.chatfilter.plugin.command.impl.ReloadCommand;
import de.netzkronehd.chatfilter.plugin.command.impl.ViolationsCommand;
import de.netzkronehd.chatfilter.plugin.config.ConfigLoader;
import de.netzkronehd.chatfilter.plugin.event.PlatformChatEvent;
import de.netzkronehd.chatfilter.plugin.listener.ChatFilterListener;
import de.netzkronehd.translation.exception.UnknownLocaleException;
import de.netzkronehd.translation.sender.spigot.SpigotSenderFactory;
import lombok.Getter;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.*;

import static de.netzkronehd.chatfilter.plugin.command.FilterCommand.registerCommand;

@Getter
public final class ChatFilterSpigot extends JavaPlugin implements FilterPlugin {

    private final Map<UUID, ChatFilterPlayer> playerCache = new HashMap<>();
    private SpigotSenderFactory senderFactory;

    private final FilterChain filterChain = new FilterChain();
    private final ChatFilterConfig filterConfig = new ChatFilterConfig();

    private ConfigLoader configLoader;
    private ChatFilterListener chatFilterListener;
    private Database database;

    @Override
    public void onEnable() {
        getLogger().info("ChatFilter is loading...");
        this.senderFactory = new SpigotSenderFactory(this);
        this.chatFilterListener = new ChatFilterListener(this);
        saveResource("blocked-patterns.yml", false);
        saveResource("filter.yml", false);
        saveResource("database.yml", false);
        saveResource("chatfilter.db", false);

        saveConfigsFromResources();


        configLoader = new SpigotConfigLoader(
                new File(getDataFolder(), "blocked-patterns.yml"),
                new File(getDataFolder(), "filter.yml"),
                new File(getDataFolder(), "database.yml")
        );
        try {
            getLogger().info("Reading config and connecting to database...");
            reload();
        } catch (SQLException | UnknownLocaleException | IOException e) {
            throw new RuntimeException(e);
        }

        registerCommands();
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);

        Messages.NO_PERMISSION.send(senderFactory.wrap(getServer().getConsoleSender()));
        GlobalTranslator.translator().sources().forEach((source) -> getLogger().info("Loaded translation source: "+source));

    }

    @Override
    public void registerCommands() {
        final BaseCommand baseCommand = new BaseCommand();

        registerCommand(baseCommand);
        registerCommand(new ParseCommand(this));
        registerCommand(new ReloadCommand(this));
        registerCommand(new ViolationsCommand(this));

        getCommand("netzchatfilter").setExecutor(new ChatFilterCommand(this, baseCommand));
        getCommand("netzchatfilter").setAliases(List.of("chatfilter", "ncf", "cf"));

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

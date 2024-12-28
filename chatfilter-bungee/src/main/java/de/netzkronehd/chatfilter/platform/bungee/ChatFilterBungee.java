package de.netzkronehd.chatfilter.platform.bungee;

import de.netzkronehd.chatfilter.chain.FilterChain;
import de.netzkronehd.chatfilter.config.ChatFilterConfig;
import de.netzkronehd.chatfilter.database.Database;
import de.netzkronehd.chatfilter.exception.NoFilterChainException;
import de.netzkronehd.chatfilter.platform.bungee.config.BungeeConfigLoader;
import de.netzkronehd.chatfilter.platform.bungee.listener.ChatListener;
import de.netzkronehd.chatfilter.platform.bungee.listener.PlayerListener;
import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.plugin.FilterPlugin;
import de.netzkronehd.chatfilter.plugin.config.ConfigLoader;
import de.netzkronehd.chatfilter.plugin.event.PlatformChatEvent;
import de.netzkronehd.chatfilter.plugin.listener.ChatFilterListener;
import de.netzkronehd.translation.sender.bungee.BungeeSenderFactory;
import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;

@Getter
public final class ChatFilterBungee extends Plugin implements FilterPlugin {

    private final Map<UUID, ChatFilterPlayer> playerCache = new HashMap<>();
    private BungeeSenderFactory senderFactory;

    private final FilterChain filterChain = new FilterChain();
    private final ChatFilterConfig filterConfig = new ChatFilterConfig();

    private ConfigLoader configLoader;
    private ChatFilterListener chatFilterListener;
    private Database database;

    @Override
    public void onEnable() {
        getLogger().info("ChatFilter is loading...");
        this.senderFactory = new BungeeSenderFactory(this);
        this.chatFilterListener = new ChatFilterListener(this);
        saveResource("blocked-patterns.yml", false);
        saveResource("filter.yml", false);
        saveResource("database.yml", false);
        saveResource("chatfilter.db", false);

        getLogger().info("Loading config files...");

        try {
            configLoader = new BungeeConfigLoader(
                    new File(getDataFolder(), "blocked-patterns.yml"),
                    new File(getDataFolder(), "filter.yml"),
                    new File(getDataFolder(), "database.yml")
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            getLogger().info("Reading config and connecting to database...");
            reload();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        registerCommands();

        getProxy().getPluginManager().registerListener(this, new PlayerListener(this));
        getProxy().getPluginManager().registerListener(this, new ChatListener(this));

    }

    @Override
    public void registerCommands() {

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
    public void setDatabase(Database database) {
        this.database = database;
    }

    private void saveResource(@NotNull String resourcePath, boolean replace) {
        if (resourcePath.isEmpty()) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        final InputStream in = getResourceAsStream(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + getFile());
        }

        final File outFile = new File(getDataFolder(), resourcePath);
        final int lastIndex = resourcePath.lastIndexOf('/');
        final File outDir = new File(getDataFolder(), resourcePath.substring(0, Math.max(lastIndex, 0)));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists() || replace) {
                final OutputStream out = new FileOutputStream(outFile);
                final byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            } else {
                getLogger().log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
        }
    }

}

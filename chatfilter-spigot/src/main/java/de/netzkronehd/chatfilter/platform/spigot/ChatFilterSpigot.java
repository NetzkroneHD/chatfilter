package de.netzkronehd.chatfilter.platform.spigot;

import de.netzkronehd.chatfilter.chain.FilterChain;
import de.netzkronehd.chatfilter.config.ChatFilterConfig;
import de.netzkronehd.chatfilter.database.Database;
import de.netzkronehd.chatfilter.platform.spigot.command.ChatFilterCommand;
import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.plugin.FilterPlugin;
import de.netzkronehd.chatfilter.plugin.command.impl.BaseCommand;
import de.netzkronehd.chatfilter.plugin.command.impl.ParseCommand;
import de.netzkronehd.chatfilter.plugin.command.impl.ReloadCommand;
import de.netzkronehd.chatfilter.plugin.command.impl.ViolationsCommand;
import de.netzkronehd.translation.sender.spigot.SpigotSenderFactory;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

import static de.netzkronehd.chatfilter.plugin.command.FilterCommand.registerCommand;

@Getter
public final class ChatFilterSpigot extends JavaPlugin implements FilterPlugin {

    private final Map<UUID, ChatFilterPlayer> playerCache = new HashMap<>();
    private final SpigotSenderFactory senderFactory = new SpigotSenderFactory(this);

    private FilterChain filterChain;

    @Override
    public void onEnable() {
        // Plugin startup logic

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
    public void reload() {

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
    public Database getDatabase() {
        return null;
    }

    @Override
    public ChatFilterConfig getFilterConfig() {
        return null;
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

}

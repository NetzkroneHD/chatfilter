package de.netzkronehd.chatfilter.platform.spigot;

import de.netzkronehd.chatfilter.chain.FilterChain;
import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.translation.sender.spigot.SpigotSenderFactory;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Getter
public final class ChatFilterSpigot extends JavaPlugin {

    private final Map<UUID, ChatFilterPlayer> players = new HashMap<>();
    private final SpigotSenderFactory senderFactory = new SpigotSenderFactory(this);

    private FilterChain filterChain;

    @Override
    public void onEnable() {
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public Optional<ChatFilterPlayer> getPlayer(UUID uuid) {
        return Optional.ofNullable(players.get(uuid));
    }

}

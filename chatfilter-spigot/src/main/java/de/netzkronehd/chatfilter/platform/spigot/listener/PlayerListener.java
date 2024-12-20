package de.netzkronehd.chatfilter.platform.spigot.listener;

import de.netzkronehd.chatfilter.platform.spigot.ChatFilterSpigot;
import de.netzkronehd.chatfilter.platform.spigot.player.SpigotChatFilterPlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private final ChatFilterSpigot plugin;

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        plugin.getPlayers().put(e.getPlayer().getUniqueId(), new SpigotChatFilterPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        plugin.getPlayers().remove(e.getPlayer().getUniqueId());
    }

}

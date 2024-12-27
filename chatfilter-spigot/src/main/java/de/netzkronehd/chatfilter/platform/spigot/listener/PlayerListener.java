package de.netzkronehd.chatfilter.platform.spigot.listener;

import de.netzkronehd.chatfilter.platform.spigot.ChatFilterSpigot;
import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
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
        plugin.getPlayerCache().put(e.getPlayer().getUniqueId(), new ChatFilterPlayer(plugin.getSenderFactory().wrap(e.getPlayer())));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        plugin.getPlayerCache().remove(e.getPlayer().getUniqueId());
    }

}

package de.netzkronehd.chatfilter.platform.spigot.listener;

import de.netzkronehd.chatfilter.platform.spigot.ChatFilterSpigot;
import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private final ChatFilterSpigot plugin;

    @EventHandler
    public void onJoin(PlayerJoinEvent e) throws SQLException {
        plugin.getPlayerCache().put(e.getPlayer().getUniqueId(), new ChatFilterPlayer(plugin.getSenderFactory().wrap(e.getPlayer())));
        plugin.getDatabase().insertOrUpdatePlayer(e.getPlayer().getUniqueId(), e.getPlayer().getName());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        plugin.getPlayerCache().remove(e.getPlayer().getUniqueId());
    }

}

package de.netzkronehd.chatfilter.platform.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import de.netzkronehd.chatfilter.platform.velocity.ChatFilterVelocity;
import de.netzkronehd.chatfilter.player.ChatFilterPlayer;

import java.sql.SQLException;

public class PlayerListener {

    private final ChatFilterVelocity plugin;

    public PlayerListener(ChatFilterVelocity plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onJoin(PostLoginEvent e) throws SQLException {
        plugin.getPlayerCache().put(e.getPlayer().getUniqueId(), new ChatFilterPlayer(plugin.getSenderFactory().wrap(e.getPlayer())));
        plugin.getDatabase().insertOrUpdatePlayer(e.getPlayer().getUniqueId(), e.getPlayer().getUsername());
    }

    @Subscribe
    public void onQuit(PostLoginEvent e) {
        plugin.getPlayerCache().remove(e.getPlayer().getUniqueId());
    }

    @Subscribe
    public void onDisconnect(PostLoginEvent e) {
        plugin.getPlayerCache().remove(e.getPlayer().getUniqueId());
    }

}

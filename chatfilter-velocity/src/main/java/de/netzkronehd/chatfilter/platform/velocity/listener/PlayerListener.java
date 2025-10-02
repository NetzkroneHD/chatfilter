package de.netzkronehd.chatfilter.platform.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
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
        final ChatFilterPlayer player = new ChatFilterPlayer(plugin.getSenderFactory().wrap(e.getPlayer()));
        plugin.getPlayerCache().put(e.getPlayer().getUniqueId(), player);
        plugin.callJoinEvent(player);
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent e) {
        plugin.getPlayerCache().remove(e.getPlayer().getUniqueId());
    }

}

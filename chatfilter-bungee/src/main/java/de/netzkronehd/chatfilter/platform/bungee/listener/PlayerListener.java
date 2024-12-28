package de.netzkronehd.chatfilter.platform.bungee.listener;

import de.netzkronehd.chatfilter.platform.bungee.ChatFilterBungee;
import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerListener implements Listener {

    private final ChatFilterBungee plugin;

    public PlayerListener(ChatFilterBungee plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent e) {
        plugin.getPlayerCache().put(e.getPlayer().getUniqueId(), new ChatFilterPlayer(plugin.getSenderFactory().wrap(e.getPlayer())));
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent e) {
        plugin.getPlayerCache().remove(e.getPlayer().getUniqueId());
    }

}

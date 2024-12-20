package de.netzkronehd.chatfilter.platform.spigot.listener;

import de.netzkronehd.chatfilter.platform.spigot.ChatFilterSpigot;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@RequiredArgsConstructor
public class ChatListener implements Listener {

    private final ChatFilterSpigot plugin;
    private final boolean stopOnBlock;

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if(plugin.getFilterChain() == null) {
            plugin.getLogger().warning("FilterProcessorChain is null, ignoring chat event.");
            return;
        }
        plugin.getPlayer(e.getPlayer().getUniqueId()).ifPresentOrElse(player -> {
            if(plugin.getFilterChain().process(player, e.getMessage(), stopOnBlock).isBlocked()) {
                e.setCancelled(true);
            }
        }, () -> plugin.getLogger().warning("Player not found in player map."));

    }

}

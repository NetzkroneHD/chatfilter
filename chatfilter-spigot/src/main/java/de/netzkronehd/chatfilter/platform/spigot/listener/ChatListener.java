package de.netzkronehd.chatfilter.platform.spigot.listener;

import de.netzkronehd.chatfilter.exception.NoFilterChainException;
import de.netzkronehd.chatfilter.platform.spigot.ChatFilterSpigot;
import de.netzkronehd.chatfilter.plugin.event.ChatEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@RequiredArgsConstructor
public class ChatListener implements Listener {

    private final ChatFilterSpigot plugin;

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if(plugin.getFilterChain() == null) {
            plugin.getLogger().warning("FilterProcessorChain is null, ignoring chat event.");
            return;
        }
        plugin.getPlayer(e.getPlayer()).ifPresentOrElse(player -> {
            try {
                final ChatEvent event = new ChatEvent(player, e.getMessage());
                plugin.callChatEvent(event);
                if(event.isCancelled()) {
                    e.setCancelled(true);
                    return;
                }
                if(event.getFilteredMessage() != null) {
                    e.setMessage(event.getFilteredMessage());
                }
            } catch (NoFilterChainException ex) {
                throw new RuntimeException(ex);
            }
        }, () -> plugin.getLogger().severe("Player not found in player map."));

    }

}

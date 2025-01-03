package de.netzkronehd.chatfilter.platform.bungee.listener;

import de.netzkronehd.chatfilter.exception.NoFilterChainException;
import de.netzkronehd.chatfilter.platform.bungee.ChatFilterBungee;
import de.netzkronehd.chatfilter.plugin.event.PlatformChatEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ChatListener implements Listener {

    private final ChatFilterBungee plugin;

    public ChatListener(ChatFilterBungee plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(ChatEvent e) {
        if(e.isCommand()) return;
        if(e.isCancelled()) return;
        if(plugin.getFilterChain() == null) {
            plugin.getLogger().warning("FilterProcessorChain is null, ignoring chat event.");
            return;
        }
        if(!(e.getSender() instanceof ProxiedPlayer proxiedPlayer)) {
            return;
        }

        plugin.getPlayer(proxiedPlayer).ifPresentOrElse(player -> {
            try {
                final PlatformChatEvent event = new PlatformChatEvent(player, e.getMessage());
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
        }, () -> plugin.getLogger().severe("Player not found in proxiedPlayer map."));
    }


}

package de.netzkronehd.chatfilter.platform.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.Player;
import de.netzkronehd.chatfilter.exception.NoFilterChainException;
import de.netzkronehd.chatfilter.platform.velocity.ChatFilterVelocity;
import de.netzkronehd.chatfilter.plugin.event.PlatformChatEvent;
import lombok.RequiredArgsConstructor;

import static com.velocitypowered.api.event.player.PlayerChatEvent.ChatResult.denied;
import static com.velocitypowered.api.event.player.PlayerChatEvent.ChatResult.message;

@RequiredArgsConstructor
public class ChatListener {

    private final ChatFilterVelocity plugin;

    @Subscribe
    public void onChat(PlayerChatEvent e) {
        if(plugin.getFilterChain() == null) {
            plugin.getPluginLogger().warn("FilterProcessorChain is null, ignoring chat event.");
            return;
        }
        plugin.getPlayer(e.getPlayer()).ifPresentOrElse(player -> {
            try {
                final PlatformChatEvent event = new PlatformChatEvent(player, e.getMessage());
                plugin.callChatEvent(event);
                if(event.isCancelled()) {
                    if (isChatMutationAllowed(e.getPlayer())) {
                        e.setResult(denied());
                    } else {
                        plugin.getPluginLogger().warn("Player {} send a message that was detected as 'blocked' but due to his client version being above 1.19.1 we can't block it. Please consider using the spigot plugin.", e.getPlayer().getUsername());
                    }
                    return;
                }
                if(event.getFilteredMessage() != null) {
                    if(isChatMutationAllowed(e.getPlayer())) {
                        e.setResult(message(event.getFilteredMessage()));
                    } else {
                        plugin.getPluginLogger().warn("Player {} send a message that was detected as 'filtered' but due to his client version being above 1.19.1 we can't filter it. Please consider using the spigot plugin.", e.getPlayer().getUsername());
                    }
                }
            } catch (NoFilterChainException ex) {
                throw new RuntimeException(ex);
            }
        }, () -> plugin.getPluginLogger().error("Player not found in player map."));
    }

    private boolean isChatMutationAllowed(Player player) {
        return ProtocolVersion.MINECRAFT_1_19_1.getProtocol() > player.getProtocolVersion().getProtocol();
    }

}

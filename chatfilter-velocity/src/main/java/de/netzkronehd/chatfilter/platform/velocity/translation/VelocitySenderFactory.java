package de.netzkronehd.chatfilter.platform.velocity.translation;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.netzkronehd.chatfilter.locale.translation.sender.Sender;
import de.netzkronehd.chatfilter.locale.translation.sender.SenderFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

import java.util.UUID;

public class VelocitySenderFactory extends SenderFactory<CommandSource> {

    private final ProxyServer proxyServer;

    public VelocitySenderFactory(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    @Override
    public void sendMessage(CommandSource sender, Component message) {
        sender.sendMessage(message);
    }

    @Override
    public void performCommand(CommandSource sender, String command) {
        proxyServer.getCommandManager().executeAsync(sender, command);
    }

    @Override
    public void showTitle(CommandSource sender, Title title) {
        sender.showTitle(title);
    }

    @Override
    public void resetTitle(CommandSource sender) {
        sender.resetTitle();
    }

    @Override
    public UUID getUniqueId(CommandSource sender) {
        if (sender instanceof final Player p) {
            return p.getUniqueId();
        }
        return Sender.CONSOLE_UUID;
    }

    @Override
    public String getName(CommandSource sender) {
        if (sender instanceof final Player p) {
            return p.getUsername();
        }
        return Sender.CONSOLE_NAME;
    }

    @Override
    public boolean hasPermission(CommandSource sender, String permission) {
        return sender.hasPermission(permission);
    }

    @Override
    public boolean isConsole(CommandSource sender) {
        return sender instanceof ConsoleCommandSource;
    }
}

package de.netzkronehd.chatfilter.platform.bungee.translation;

import de.netzkronehd.chatfilter.locale.translation.sender.Sender;
import de.netzkronehd.chatfilter.locale.translation.sender.SenderFactory;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.UUID;

public class BungeeSenderFactory extends SenderFactory<CommandSender> {

    private final Plugin plugin;
    private final BungeeAudiences audiences;

    public BungeeSenderFactory(Plugin plugin) {
        this.plugin = plugin;
        this.audiences = BungeeAudiences.create(plugin);
    }

    @Override
    public UUID getUniqueId(CommandSender sender) {
        if(sender instanceof final ProxiedPlayer p) {
            return p.getUniqueId();
        }
        return Sender.CONSOLE_UUID;
    }

    @Override
    public String getName(CommandSender sender) {
        if(sender instanceof ProxiedPlayer) {
            return sender.getName();
        }
        return Sender.CONSOLE_NAME;
    }

    @Override
    public void sendMessage(CommandSender sender, Component message) {
        this.audiences.sender(sender).sendMessage(message);
    }

    @Override
    public void performCommand(CommandSender sender, String command) {
        this.plugin.getProxy().getPluginManager().dispatchCommand(sender, command);
    }

    @Override
    public void showTitle(CommandSender sender, Title title) {
        this.audiences.sender(sender).showTitle(title);
    }

    @Override
    public void resetTitle(CommandSender sender) {
        this.audiences.sender(sender).resetTitle();
    }

    @Override
    public boolean hasPermission(CommandSender sender, String permission) {
        return sender.hasPermission(permission);
    }

    @Override
    public boolean isConsole(CommandSender sender) {
        return !(sender instanceof ProxiedPlayer);
    }
}

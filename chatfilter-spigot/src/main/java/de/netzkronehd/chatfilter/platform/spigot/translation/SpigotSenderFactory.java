package de.netzkronehd.chatfilter.platform.spigot.translation;

import de.netzkronehd.chatfilter.translation.sender.Sender;
import de.netzkronehd.chatfilter.translation.sender.SenderFactory;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class SpigotSenderFactory extends SenderFactory<CommandSender> {

    private final JavaPlugin plugin;
    private final BukkitAudiences audiences;

    public SpigotSenderFactory(JavaPlugin plugin) {
        this.plugin = plugin;
        this.audiences = BukkitAudiences.create(plugin);
    }

    @Override
    public void sendMessage(CommandSender sender, Component message) {
        this.audiences.sender(sender).sendMessage(message);
    }

    @Override
    public void performCommand(CommandSender sender, String command) {
        this.plugin.getServer().dispatchCommand(sender, command);
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
    public UUID getUniqueId(CommandSender sender) {
        if(sender instanceof final Player p) {
            return p.getUniqueId();
        }
        return Sender.CONSOLE_UUID;
    }

    @Override
    public String getName(CommandSender sender) {
        if(sender instanceof Player) {
            return sender.getName();
        }
        return Sender.CONSOLE_NAME;
    }

    @Override
    public boolean hasPermission(CommandSender sender, String permission) {
        return sender.hasPermission(permission);
    }

    @Override
    public boolean isConsole(CommandSender sender) {
        return sender instanceof ConsoleCommandSender;
    }
}

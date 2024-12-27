package de.netzkronehd.chatfilter.platform.spigot.command;

import de.netzkronehd.chatfilter.platform.spigot.ChatFilterSpigot;
import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.plugin.command.impl.BaseCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ChatFilterCommand implements CommandExecutor, TabCompleter {

    private final ChatFilterSpigot plugin;
    private final BaseCommand baseCommand;

    public ChatFilterCommand(ChatFilterSpigot plugin, BaseCommand baseCommand) {
        this.plugin = plugin;
        this.baseCommand = baseCommand;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof final Player player) {
            baseCommand.execute(plugin.getPlayer(player).orElseThrow(), args);
        } else {
            baseCommand.execute(new ChatFilterPlayer(plugin.getSenderFactory().wrap(sender)), args);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof final Player player) {
            return baseCommand.tabComplete(plugin.getPlayer(player).orElseThrow(), args);
        } else {
            return baseCommand.tabComplete(new ChatFilterPlayer(plugin.getSenderFactory().wrap(sender)), args);
        }
    }
}

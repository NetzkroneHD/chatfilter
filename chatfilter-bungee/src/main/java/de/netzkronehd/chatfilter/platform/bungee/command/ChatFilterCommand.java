package de.netzkronehd.chatfilter.platform.bungee.command;

import de.netzkronehd.chatfilter.platform.bungee.ChatFilterBungee;
import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.plugin.command.impl.BaseCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class ChatFilterCommand extends Command implements TabExecutor {

    private final ChatFilterBungee plugin;
    private final BaseCommand baseCommand;

    public ChatFilterCommand(ChatFilterBungee plugin, BaseCommand baseCommand) {
        super("netzchatfilter", null, "chatfilter", "ncf", "nc");
        this.plugin = plugin;
        this.baseCommand = baseCommand;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof final ProxiedPlayer player) {
            baseCommand.execute(plugin.getPlayer(player).orElseThrow(), args);
        } else {
            baseCommand.execute(new ChatFilterPlayer(plugin.getSenderFactory().wrap(sender)), args);
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if(sender instanceof final ProxiedPlayer player) {
            return baseCommand.tabComplete(plugin.getPlayer(player).orElseThrow(), args);
        } else {
            return baseCommand.tabComplete(new ChatFilterPlayer(plugin.getSenderFactory().wrap(sender)), args);
        }
    }
}

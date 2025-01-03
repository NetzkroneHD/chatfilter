package de.netzkronehd.chatfilter.platform.velocity.command;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import de.netzkronehd.chatfilter.platform.velocity.ChatFilterVelocity;
import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.plugin.command.impl.BaseCommand;

import java.util.List;

public class ChatFilterCommand implements SimpleCommand {

    private final ChatFilterVelocity plugin;
    private final BaseCommand baseCommand;

    public ChatFilterCommand(ChatFilterVelocity plugin, BaseCommand baseCommand) {
        this.plugin = plugin;
        this.baseCommand = baseCommand;
    }

    @Override
    public void execute(Invocation invocation) {
        if(invocation.source() instanceof final Player player) {
            baseCommand.execute(plugin.getPlayer(player).orElseThrow(), invocation.arguments());
        } else {
            baseCommand.execute(new ChatFilterPlayer(plugin.getSenderFactory().wrap(invocation.source())), invocation.arguments());
        }
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        if(invocation.source() instanceof final Player player) {
            return baseCommand.tabComplete(plugin.getPlayer(player).orElseThrow(), invocation.arguments());
        } else {
            return baseCommand.tabComplete(new ChatFilterPlayer(plugin.getSenderFactory().wrap(invocation.source())), invocation.arguments());
        }
    }
}

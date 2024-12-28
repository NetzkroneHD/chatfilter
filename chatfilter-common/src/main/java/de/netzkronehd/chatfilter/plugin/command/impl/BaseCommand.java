package de.netzkronehd.chatfilter.plugin.command.impl;

import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.plugin.command.FilterCommand;

import java.util.List;

import static de.netzkronehd.chatfilter.locale.Messages.BASE_USAGE;
import static de.netzkronehd.chatfilter.locale.Messages.NO_PERMISSION;
import static java.util.Collections.emptyList;

public class BaseCommand implements FilterCommand {

    @Override
    public void execute(ChatFilterPlayer chatFilterPlayer, String[] args) {
        if(!hasPermission(chatFilterPlayer)) {
            NO_PERMISSION.send(chatFilterPlayer.getSender());
            return;
        }
        if(args.length < 1) {
            BASE_USAGE.send(chatFilterPlayer.getSender());
            return;
        }

        final String subCommand = args[0];
        FilterCommand.executeSubCommand(subCommand, chatFilterPlayer, args);
    }

    @Override
    public List<String> tabComplete(ChatFilterPlayer chatFilterPlayer, String[] args) {
        if(!hasPermission(chatFilterPlayer) || args.length == 0) {
            return emptyList();
        }
        final String command = args[0].toLowerCase();
        if (args.length == 1) {
            return FilterCommand.COMMANDS.keySet().stream().filter(s -> s.startsWith(command)).toList();
        }
        return FilterCommand.executeSubTab(command, chatFilterPlayer, args);
    }

    @Override
    public String getName() {
        return "chatfilter";
    }
}

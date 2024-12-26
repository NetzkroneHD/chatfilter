package de.netzkronehd.chatfilter.plugin.command.impl;

import de.netzkronehd.chatfilter.locale.Messages;
import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.plugin.command.FilterCommand;

import java.util.List;

import static de.netzkronehd.chatfilter.locale.Messages.NO_PERMISSION;

public class BaseCommand implements FilterCommand {

    @Override
    public void execute(ChatFilterPlayer chatFilterPlayer, String[] args) {
        if(!hasPermission(chatFilterPlayer)) {
            NO_PERMISSION.send(chatFilterPlayer.getSender());
            return;
        }
        if(args.length < 1) {
            Messages.BASE_USAGE.send(chatFilterPlayer.getSender());
            return;
        }

        final String subCommand = args[0];
        FilterCommand.executeSubCommand(subCommand, chatFilterPlayer, args);
    }

    @Override
    public List<String> tabComplete(ChatFilterPlayer chatFilterPlayer, String[] args) {
        return FilterCommand.super.tabComplete(chatFilterPlayer, args);
    }

    @Override
    public String getName() {
        return "chatfilter";
    }
}

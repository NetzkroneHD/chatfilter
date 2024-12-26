package de.netzkronehd.chatfilter.plugin.command;

import de.netzkronehd.chatfilter.locale.Messages;
import de.netzkronehd.chatfilter.player.ChatFilterPlayer;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface FilterCommand {

    Map<String, FilterCommand> COMMANDS = new HashMap<>();

    void execute(ChatFilterPlayer chatFilterPlayer, String[] args);

    default List<String> tabComplete(ChatFilterPlayer chatFilterPlayer, String[] args) {
        return Collections.emptyList();
    }

    String getName();

    default String getPermission() {
        return "chatfilter.command." + getName();
    }

    default boolean hasPermission(ChatFilterPlayer chatFilterPlayer) {
        return chatFilterPlayer.getSender().hasPermission(getPermission()) ||
                chatFilterPlayer.getSender().hasPermission("chatfilter.command.*") ||
                chatFilterPlayer.getSender().hasPermission("chatfilter.*");
    }

    static void registerCommand(FilterCommand baseCommand) {
        COMMANDS.put(baseCommand.getName().toLowerCase(), baseCommand);
    }

    static void executeSubCommand(String command, ChatFilterPlayer chatFilterPlayer, String[] args) {
        final FilterCommand subCommand = COMMANDS.get(command.toLowerCase());
        if(subCommand == null) {
            Messages.BASE_USAGE.send(chatFilterPlayer.getSender());
            return;
        }
        final String[] subArgs = new String[args.length - 1];
        System.arraycopy(args, 1, subArgs, 0, subArgs.length);
        subCommand.execute(chatFilterPlayer, subArgs);
    }

}

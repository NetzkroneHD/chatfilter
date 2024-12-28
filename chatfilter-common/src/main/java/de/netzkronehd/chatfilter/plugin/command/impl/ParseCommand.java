package de.netzkronehd.chatfilter.plugin.command.impl;

import de.netzkronehd.chatfilter.chain.FilterChainResult;
import de.netzkronehd.chatfilter.exception.FilterNotFoundException;
import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.plugin.FilterPlugin;
import de.netzkronehd.chatfilter.plugin.command.FilterCommand;

import java.util.ArrayList;
import java.util.List;

import static de.netzkronehd.chatfilter.locale.Messages.*;
import static de.netzkronehd.chatfilter.utils.Utils.getArgsAsText;
import static java.util.Collections.emptyList;

public class ParseCommand implements FilterCommand {

    private final FilterPlugin filterPlugin;

    public ParseCommand(FilterPlugin filterPlugin) {
        this.filterPlugin = filterPlugin;
    }

    @Override
    public void execute(ChatFilterPlayer chatFilterPlayer, String[] args) {
        if(!hasPermission(chatFilterPlayer)) {
            NO_PERMISSION.send(chatFilterPlayer.getSender());
            return;
        }
        if(args.length < 2) {
            PARSE_USAGE.send(chatFilterPlayer.getSender());
            return;
        }
        final String filterName = args[0];
        final String message = getArgsAsText(args, 1);
        try {
            final FilterChainResult result;
            if (filterName.equalsIgnoreCase("all")) {
                result = filterPlugin.getFilterChain().process(chatFilterPlayer, message);
            } else {
                result = filterPlugin.getFilterChain().process(chatFilterPlayer, message, filterName);
            }
            PARSE_RESULT.send(chatFilterPlayer.getSender(), result);
        } catch (FilterNotFoundException ex) {
            FILTER_NOT_FOUND.send(chatFilterPlayer.getSender(), filterName);
        }

    }

    @Override
    public List<String> tabComplete(ChatFilterPlayer chatFilterPlayer, String[] args) {
        if(!hasPermission(chatFilterPlayer) || args.length != 1) {
            return emptyList();
        }
        final List<String> tabs = new ArrayList<>();
        final String prefix = args[0].toLowerCase();

        if("all".startsWith(prefix)) tabs.add("all");
        tabs.addAll(filterPlugin.getFilterChain().getProcessors().stream()
                .map(processor -> processor.getName().toLowerCase())
                .filter(name -> name.startsWith(prefix)).toList());
        return tabs;
    }

    @Override
    public String getName() {
        return "parse";
    }
}

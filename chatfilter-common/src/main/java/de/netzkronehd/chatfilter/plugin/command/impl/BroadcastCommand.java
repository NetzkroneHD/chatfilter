package de.netzkronehd.chatfilter.plugin.command.impl;

import de.netzkronehd.chatfilter.database.model.BroadcastType;
import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.player.ReceiveBroadcastType;
import de.netzkronehd.chatfilter.plugin.FilterPlugin;
import de.netzkronehd.chatfilter.plugin.command.FilterCommand;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static de.netzkronehd.chatfilter.database.model.BroadcastType.of;
import static de.netzkronehd.chatfilter.locale.Messages.*;

public class BroadcastCommand implements FilterCommand {

    private final FilterPlugin plugin;

    public BroadcastCommand(FilterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(ChatFilterPlayer chatFilterPlayer, String[] args) {
        if(!hasPermission(chatFilterPlayer)) {
            COMMAND_NO_PERMISSION.send(chatFilterPlayer.getSender());
            return;
        }
        if(args.length != 2) {
            COMMAND_BROADCAST_USAGE.send(chatFilterPlayer.getSender());
            return;
        }
        try {
            final ReceiveBroadcastType type = ReceiveBroadcastType.valueOf(args[1].toUpperCase());
            if("filter".equalsIgnoreCase(args[0])) {
                chatFilterPlayer.setFilteredBroadcastType(type);
            } else if("block".equalsIgnoreCase(args[0])) {
                chatFilterPlayer.setBlockedBroadcastType(type);
            } else {
                COMMAND_BROADCAST_USAGE.send(chatFilterPlayer.getSender());
                return;
            }
            final BroadcastType broadcastType = of(chatFilterPlayer.getFilteredBroadcastType(), chatFilterPlayer.getBlockedBroadcastType());
            plugin.getDatabase().updateBroadcastType(chatFilterPlayer.getSender().getUniqueId(), broadcastType);

            COMMAND_BROADCAST_SUCCESS.send(chatFilterPlayer.getSender(), args[0], type);
        } catch (IllegalArgumentException ex) {
            COMMAND_BROADCAST_USAGE.send(chatFilterPlayer.getSender());
        } catch (SQLException ex) {
            ERROR.send(chatFilterPlayer.getSender(), ex);
        }

    }

    @Override
    public List<String> tabComplete(ChatFilterPlayer chatFilterPlayer, String[] args) {
        if(!hasPermission(chatFilterPlayer)) {
            return Collections.emptyList();
        }
        if(args.length == 1) {
            final List<String> tabs = new ArrayList<>();
            final String prefix = args[0].toLowerCase();

            if("filter".startsWith(prefix)) tabs.add("filter");
            if("block".startsWith(prefix)) tabs.add("block");
            return tabs;
        }
        if (args.length == 2) {
            final List<String> tabs = new ArrayList<>();
            final String prefix = args[1].toLowerCase();
            for (ReceiveBroadcastType type : ReceiveBroadcastType.values()) {
                if(type.name().toLowerCase().startsWith(prefix)) {
                    tabs.add(type.name().toLowerCase());
                }
            }
            return tabs;
        }
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return "broadcast";
    }
}

package de.netzkronehd.chatfilter.plugin.command.impl;

import de.netzkronehd.chatfilter.locale.Messages;
import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.plugin.FilterPlugin;
import de.netzkronehd.chatfilter.plugin.command.FilterCommand;

import static de.netzkronehd.chatfilter.locale.Messages.*;

public class ReloadCommand implements FilterCommand {

    private final FilterPlugin filterPlugin;

    public ReloadCommand(FilterPlugin filterPlugin) {
        this.filterPlugin = filterPlugin;
    }

    @Override
    public void execute(ChatFilterPlayer chatFilterPlayer, String[] args) {
        if(!hasPermission(chatFilterPlayer)) {
            NO_PERMISSION.send(chatFilterPlayer.getSender());
            return;
        }
        Messages.RELOADING.send(chatFilterPlayer.getSender());
        final long before = System.currentTimeMillis();
        try {
            filterPlugin.reload();
            RELOAD_COMPLETE.send(chatFilterPlayer.getSender(), System.currentTimeMillis() - before);
        } catch (Exception ex) {
            ERROR.send(chatFilterPlayer.getSender(), ex);
        }
    }

    @Override
    public String getName() {
        return "reload";
    }
}

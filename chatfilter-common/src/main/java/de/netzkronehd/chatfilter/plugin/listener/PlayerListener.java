package de.netzkronehd.chatfilter.plugin.listener;

import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.plugin.FilterPlugin;

import java.sql.SQLException;

public class PlayerListener {

    private final FilterPlugin plugin;

    public PlayerListener(FilterPlugin plugin) {
        this.plugin = plugin;
    }

    public void onJoin(ChatFilterPlayer player) throws SQLException {
        plugin.getDatabase().insertOrUpdatePlayer(player.getSender().getUniqueId(), player.getSender().getName());

    }

}

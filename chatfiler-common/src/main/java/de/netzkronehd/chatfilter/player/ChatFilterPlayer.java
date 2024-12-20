package de.netzkronehd.chatfilter.player;

import de.netzkronehd.translation.sender.Sender;

/**
 * Represents a player that is being filtered by the chat filter.
 */
public interface ChatFilterPlayer {

    ChatMetrics getChatMetrics();

    Sender getSender();

}

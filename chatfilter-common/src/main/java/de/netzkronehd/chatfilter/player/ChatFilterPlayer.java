package de.netzkronehd.chatfilter.player;

import de.netzkronehd.chatfilter.translation.sender.Sender;
import lombok.Getter;

/**
 * Represents a player that is being filtered by the chat filter.
 */
@Getter
public class ChatFilterPlayer {

    private final ChatMetrics chatMetrics;
    private final Sender sender;

    public ChatFilterPlayer(Sender sender) {
        this.chatMetrics = new ChatMetrics();
        this.sender = sender;
    }

    public ChatFilterPlayer(ChatMetrics chatMetrics, Sender sender) {
        this.chatMetrics = chatMetrics;
        this.sender = sender;
    }
}

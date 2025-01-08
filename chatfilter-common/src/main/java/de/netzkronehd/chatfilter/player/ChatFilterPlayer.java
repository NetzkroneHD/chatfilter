package de.netzkronehd.chatfilter.player;

import de.netzkronehd.chatfilter.locale.translation.sender.Sender;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a player that is being filtered by the chat filter.
 */
@Getter
@Setter
public class ChatFilterPlayer {

    private final ChatMetrics chatMetrics;
    private final Sender sender;

    @Nullable
    private ReceiveBroadcastType receiveBroadcastType;

    public ChatFilterPlayer(Sender sender) {
        this.chatMetrics = new ChatMetrics();
        this.sender = sender;
    }

    public ChatFilterPlayer(ChatMetrics chatMetrics, Sender sender) {
        this.chatMetrics = chatMetrics;
        this.sender = sender;
    }

}

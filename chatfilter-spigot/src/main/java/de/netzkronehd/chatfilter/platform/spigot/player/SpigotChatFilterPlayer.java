package de.netzkronehd.chatfilter.platform.spigot.player;

import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.player.ChatMetrics;
import de.netzkronehd.translation.sender.Sender;

public class SpigotChatFilterPlayer implements ChatFilterPlayer {

    private final ChatMetrics chatMetrics;
    private final Sender sender;

    public SpigotChatFilterPlayer(Sender sender) {
        this.chatMetrics = new ChatMetrics();
        this.sender = sender;
    }

    public SpigotChatFilterPlayer(ChatMetrics chatMetrics, Sender sender) {
        this.chatMetrics = chatMetrics;
        this.sender = sender;
    }

    @Override
    public ChatMetrics getChatMetrics() {
        return this.chatMetrics;
    }

    @Override
    public Sender getSender() {
        return sender;
    }
}

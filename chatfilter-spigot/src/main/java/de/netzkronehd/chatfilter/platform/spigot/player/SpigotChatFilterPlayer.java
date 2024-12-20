package de.netzkronehd.chatfilter.platform.spigot.player;

import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.player.ChatMetrics;

public class SpigotChatFilterPlayer implements ChatFilterPlayer {

    private final ChatMetrics chatMetrics;

    public SpigotChatFilterPlayer() {
        this.chatMetrics = new ChatMetrics();
    }

    public SpigotChatFilterPlayer(ChatMetrics chatMetrics) {
        this.chatMetrics = chatMetrics;
    }

    @Override
    public ChatMetrics getChatMetrics() {
        return this.chatMetrics;
    }
}

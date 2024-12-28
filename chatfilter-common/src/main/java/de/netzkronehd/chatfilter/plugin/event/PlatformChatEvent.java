package de.netzkronehd.chatfilter.plugin.event;

import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class PlatformChatEvent {

    private final ChatFilterPlayer player;
    private final String message;
    private String filteredMessage;
    private boolean cancelled;

}

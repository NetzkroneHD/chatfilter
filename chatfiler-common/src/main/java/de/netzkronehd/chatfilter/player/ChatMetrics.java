package de.netzkronehd.chatfilter.player;

import de.netzkronehd.chatfilter.chain.FilterChainResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the metrics of a player's chat.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMetrics {

    /**
     * The last message sent by the player.
     * This is the original message without any modifications.
     */
    private @Nullable String lastMessage;
    /**
     * The time the last message was sent.
     * This is the time in milliseconds since the epoch.
     * @see System#currentTimeMillis()
     */
    private @Nullable Long lastMessageTime;
    /**
     * The result of the last filter process.
     */
    private @Nullable FilterChainResult lastFilterProcessorResult;

    private int messageCount;

    private int allowedMessageCount;
    private int filteredMessageCount;
    private int blockedMessageCount;


}

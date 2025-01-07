package de.netzkronehd.chatfilter.processor.impl;

import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.player.ChatMetrics;
import de.netzkronehd.chatfilter.processor.FilterProcessorResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SimilarityFilterTest {

    @Test
    void testProcessWithBlockedMessage() {
        // Arrange
        final SimilarityFilter similarityFilter = new SimilarityFilter("name", 1, 0.8, "reason");

        final String lastMessage = "a".repeat(100);
        final String nextMessage = "a".repeat(81)+"b".repeat(19);
        final ChatFilterPlayer player = mock(ChatFilterPlayer.class);
        final ChatMetrics chatMetrics = new ChatMetrics();
        chatMetrics.setLastMessage(lastMessage);

        when(player.getChatMetrics()).thenReturn(chatMetrics);

        // Act
        final FilterProcessorResult process = similarityFilter.process(player, null, nextMessage);

        // Assert
        assertAll(
                () -> assertNotNull(process, "Result is null"),
                () -> assertEquals("reason", process.reason(), "Reason is not correct"),
                () -> assertTrue(process.isBlocked(), "Message is not blocked")
        );
    }

    @Test
    void testProcessWithNullPlayer() {
        // Arrange
        final SimilarityFilter similarityFilter = new SimilarityFilter("name", 1, 0.8, "reason");

        // Act
        // Assert
        assertThrows(NullPointerException.class, () -> similarityFilter.process(null, null, "message"));
    }

    @Test
    void testProcessWithNullMessage() {
        // Arrange
        final SimilarityFilter similarityFilter = new SimilarityFilter("name", 1, 0.8, "reason");
        final String previousMessage = "a".repeat(100);
        final ChatFilterPlayer player = mock(ChatFilterPlayer.class);
        final ChatMetrics chatMetrics = new ChatMetrics();
        chatMetrics.setLastMessage(previousMessage);

        when(player.getChatMetrics()).thenReturn(chatMetrics);

        // Act
        // Assert
        assertThrows(NullPointerException.class, () -> similarityFilter.process(player, null, null));
    }

    @Test
    void testProcessWithNullLastMessage() {
        // Arrange
        final SimilarityFilter similarityFilter = new SimilarityFilter("name", 1, 0.8, "reason");

        final String nextMessage = "a".repeat(81)+"b".repeat(19);
        final ChatFilterPlayer player = mock(ChatFilterPlayer.class);
        final ChatMetrics chatMetrics = new ChatMetrics();
        chatMetrics.setLastMessage(null);

        when(player.getChatMetrics()).thenReturn(chatMetrics);

        // Act
        final FilterProcessorResult process = similarityFilter.process(player, null, nextMessage);

        // Assert
        assertAll(
                () -> assertNotNull(process, "Result is null"),
                () -> assertTrue(process.isAllowed(), "Message is not blocked"),
                () -> assertNotNull(process.reason(), "Reason is null")
        );
    }
}

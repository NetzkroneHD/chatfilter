package de.netzkronehd.chatfilter.processor.impl;

import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.player.ChatMetrics;
import de.netzkronehd.chatfilter.processor.FilterProcessorResult;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LastMessageTimeFilterTest {

    @Test
    void testProcessWithBlockedMessage() {
        // Arrange
        final LastMessageTimeFilter lastMessageTimeFilter = new LastMessageTimeFilter(
                "name",
                1,
                TimeUnit.SECONDS.toMillis(2),
                "reason"
        );
        final ChatFilterPlayer player = mock(ChatFilterPlayer.class);
        final ChatMetrics chatMetrics = new ChatMetrics();
        chatMetrics.setLastMessageTime(System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(1));

        when(player.getChatMetrics()).thenReturn(chatMetrics);

        // Act
        final FilterProcessorResult result = lastMessageTimeFilter.process(player, null, "message");

        // Assert

        assertAll(
                () -> assertNotNull(result, "Result is null"),
                () -> assertEquals("reason", result.reason(), "Reason is not correct"),
                () -> assertTrue(result.isBlocked(), "Message is not blocked")
        );

    }

    @Test
    void testProcessWithNullPlayer() {
        // Arrange
        final LastMessageTimeFilter lastMessageTimeFilter = new LastMessageTimeFilter(
                "name",
                1,
                TimeUnit.SECONDS.toMillis(2),
                "reason"
        );

        // Act
        // Assert
        assertThrows(NullPointerException.class, () -> lastMessageTimeFilter.process(null, null, "message"));
    }

    @Test
    void testProcessWithAllowedMessage() {
        // Arrange
        final LastMessageTimeFilter lastMessageTimeFilter = new LastMessageTimeFilter(
                "name",
                1,
                TimeUnit.SECONDS.toMillis(2),
                "reason"
        );
        final ChatFilterPlayer player = mock(ChatFilterPlayer.class);
        final ChatMetrics chatMetrics = new ChatMetrics();
        chatMetrics.setLastMessageTime(System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(3));

        when(player.getChatMetrics()).thenReturn(chatMetrics);

        // Act
        final FilterProcessorResult result = lastMessageTimeFilter.process(player, null, "message");

        // Assert
        assertAll(
                () -> assertNotNull(result, "Result is null"),
                () -> assertTrue(result.isAllowed(), "Message is not allowed"),
                () -> assertNotNull(result.reason(), "Reason is null")
        );

    }
}

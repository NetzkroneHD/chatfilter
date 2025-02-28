package de.netzkronehd.chatfilter.processor.impl;

import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.player.ChatMetrics;
import de.netzkronehd.chatfilter.processor.FilterProcessorResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TooManyViolationsFilterTest {

    @Test
    void testProcessWithBlockedMessage() {
        // Arrange
        final TooManyViolationsFilter tooManyViolationsFilter = new TooManyViolationsFilter("name", 1, "reason", 0.8, 0);
        final ChatFilterPlayer player = mock(ChatFilterPlayer.class);
        final ChatMetrics chatMetrics = new ChatMetrics();

        chatMetrics.setTotalMessageCount(100);
        chatMetrics.setBlockedMessageCount(80);
        chatMetrics.setAllowedMessageCount(20);

        when(player.getChatMetrics()).thenReturn(chatMetrics);

        // Act
        final FilterProcessorResult result = tooManyViolationsFilter.process(player, null, "message");

        // Assert
        assertAll(
                () -> assertNotNull(result, "Result is null"),
                () -> assertEquals("reason", result.reason(), "Reason is not correct"),
                () -> assertTrue(result.isBlocked(), "Message is not blocked")
        );
    }

    @Test
    void testProcessWithAllowedMessage() {
        // Arrange
        final TooManyViolationsFilter tooManyViolationsFilter = new TooManyViolationsFilter("name", 1, "reason", 0.8, 0);
        final ChatFilterPlayer player = mock(ChatFilterPlayer.class);
        final ChatMetrics chatMetrics = new ChatMetrics();

        chatMetrics.setTotalMessageCount(100);
        chatMetrics.setBlockedMessageCount(79);
        chatMetrics.setAllowedMessageCount(21);

        when(player.getChatMetrics()).thenReturn(chatMetrics);

        // Act
        final FilterProcessorResult result = tooManyViolationsFilter.process(player, null, "message");

        // Assert
        assertAll(
                () -> assertNotNull(result, "Result is null"),
                () -> assertNotNull(result.reason(), "Reason is null"),
                () -> assertTrue(result.isAllowed(), "Message is not allowed")
        );
    }
}

package de.netzkronehd.chatfilter.processor.impl;

import de.netzkronehd.chatfilter.message.MessageState;
import de.netzkronehd.chatfilter.processor.FilterProcessorResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.regex.Pattern.compile;
import static org.junit.jupiter.api.Assertions.*;

class BlockedPatternFilterTest {

    @Test
    void testProcessWithBlockedMessageState() {
        // Arrange
        final String testMessage = "This is a message that contains the word replace.";
        final BlockedPatternFilter blockedPatternFilter = new BlockedPatternFilter(
                "name",
                1,
                List.of(compile("replace")),
                "reason",
                MessageState.BLOCKED,
                '*');
        // Act
        final FilterProcessorResult result = blockedPatternFilter.process(null, null, testMessage);

        // Assert
        assertTrue(result.isBlocked());
    }

    @Test
    void testProcessWithFilteredMessageState() {
        // Arrange
        final String testMessage = "This is a message that contains the word replace.";
        final BlockedPatternFilter blockedPatternFilter = new BlockedPatternFilter(
                "name",
                1,
                List.of(compile("replace")),
                "reason",
                MessageState.FILTERED,
                '*');
        // Act
        final FilterProcessorResult result = blockedPatternFilter.process(null, null, testMessage);

        // Assert
        assertAll(
                () -> assertTrue(result.isFiltered(), "Message is not filtered"),
                () -> assertNotNull(result.filteredMessage(), "Filtered message is null"),
                () -> assertTrue(result.filteredMessage().isPresent(), "Filtered message is not present"),
                () -> assertEquals("This is a message that contains the word *******.", result.filteredMessage().get(), "Filtered message did not replace the word")
        );
    }

    @Test
    void testProcessWithFilteredMessageStateAndMultiplePatterns() {
        // Arrange
        final String testMessage = "This is a message that contains the word replace and should replace this too.";
        final BlockedPatternFilter blockedPatternFilter = new BlockedPatternFilter(
                "name",
                1,
                List.of(compile("replace"), compile("this")),
                "reason",
                MessageState.FILTERED,
                '*');
        // Act
        final FilterProcessorResult result = blockedPatternFilter.process(null, null, testMessage);

        // Assert
        assertAll(
                () -> assertTrue(result.isFiltered(), "Message is not filtered"),
                () -> assertNotNull(result.filteredMessage(), "Filtered message is null"),
                () -> assertTrue(result.filteredMessage().isPresent(), "Filtered message is not present"),
                () -> assertEquals("**** is a message that contains the word ******* and should ******* **** too.", result.filteredMessage().get(), "Filtered message did not replace the word")
        );
    }
}

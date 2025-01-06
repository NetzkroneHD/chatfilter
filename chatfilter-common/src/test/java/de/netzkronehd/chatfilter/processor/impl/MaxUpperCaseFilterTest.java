package de.netzkronehd.chatfilter.processor.impl;

import de.netzkronehd.chatfilter.processor.FilterProcessorResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MaxUpperCaseFilterTest {

    @Test
    void testProcessWithMaxUpperCase() {
        // Arrange
        final String testMessage = "A".repeat(40)+"a".repeat(60);
        final MaxUpperCaseFilter maxUpperCaseFilter = new MaxUpperCaseFilter("name", 1, 10, 0.4, "reason");

        // Act
        final FilterProcessorResult process = maxUpperCaseFilter.process(null, null, testMessage);

        // Assert
        assertTrue(process.isBlocked(), "Message is not blocked");
    }

    @Test
    void testProcessWithNoMaxUpperCase() {
        // Arrange
        final String testMessage = "A".repeat(39)+"a".repeat(61);
        final MaxUpperCaseFilter maxUpperCaseFilter = new MaxUpperCaseFilter("name", 1, 10, 0.4, "reason");

        // Act
        final FilterProcessorResult result = maxUpperCaseFilter.process(null, null, testMessage);

        // Assert

        assertAll(
                () -> assertTrue(result.isAllowed(), "Message is blocked"),
                () -> assertNotNull(result.reason(), "Reason is null")
        );
    }

}

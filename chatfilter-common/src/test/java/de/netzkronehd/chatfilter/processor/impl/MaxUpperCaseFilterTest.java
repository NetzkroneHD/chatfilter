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
        final FilterProcessorResult result = maxUpperCaseFilter.process(null, null, testMessage);

        // Assert
        assertAll(
                () -> assertNotNull(result, "Result is null"),
                () -> assertTrue(result.isBlocked(), "Message is not blocked"),
                () -> assertEquals("reason", result.reason(), "Reason is not correct")
        );
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
                () -> assertNotNull(result, "Result is null"),
                () -> assertTrue(result.isAllowed(), "Message is blocked"),
                () -> assertNotNull(result.reason(), "Reason is null")
        );
    }

    @Test
    void testProcessWithNullMessage() {
        // Arrange
        final MaxUpperCaseFilter maxUpperCaseFilter = new MaxUpperCaseFilter("name", 1, 10, 0.4, "reason");

        // Act
        final FilterProcessorResult result = maxUpperCaseFilter.process(null, null, null);

        // Assert
        assertAll(
                () -> assertNotNull(result, "Result is null"),
                () -> assertTrue(result.isAllowed(), "Message is not allowed"),
                () -> assertNotNull(result.reason(), "Reason is null")
        );
    }

    @Test
    void testProcessWithAllowedMessage() {
        // Arrange
        final String testMessage = "A".repeat(39)+"a".repeat(61);
        final MaxUpperCaseFilter maxUpperCaseFilter = new MaxUpperCaseFilter("name", 1, 10, 0.4, "reason");

        // Act
        final FilterProcessorResult result = maxUpperCaseFilter.process(null, null, testMessage);

        // Assert
        assertAll(
                () -> assertNotNull(result, "Result is null"),
                () -> assertTrue(result.isAllowed(), "Message is not allowed"),
                () -> assertNotNull(result.reason(), "Reason is null")
        );
    }

}

package de.netzkronehd.chatfilter.chain;

import de.netzkronehd.chatfilter.config.ChatFilterConfig;
import de.netzkronehd.chatfilter.exception.FilterNotFoundException;
import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.processor.FilterProcessor;
import de.netzkronehd.chatfilter.processor.FilterProcessorResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.netzkronehd.chatfilter.processor.FilterProcessorResult.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FilterChainTest {

    @Test
    void testProcessWithBlockedMessage() {
        // Arrange
        final FilterChain filterChain = new FilterChain();
        final FilterProcessor processor = mock(FilterProcessor.class);
        final FilterProcessorResult result = blocked("message", processor, "reason");
        filterChain.addProcessor(processor);

        when(processor.process(any(), any(), anyString())).thenReturn(result);

        // Act
        final FilterChainResult chainResult = filterChain.process(mock(ChatFilterPlayer.class), "test message");

        // Assert
        assertAll(
                () -> assertNotNull(chainResult, "Result is null"),
                () -> assertEquals(1, chainResult.getResults().size(), "Result size is not correct"),
                () -> assertTrue(chainResult.isBlocked(), "Message is not blocked"),
                () -> assertEquals(result, chainResult.getBlockedBy().get(), "Result is not correct"),
                () -> assertEquals("reason", chainResult.getBlockedReason().get(), "Blocked reason is not correct")
        );
    }

    @Test
    void testProcessWithFilteredMessage() {
        // Arrange
        final FilterChain filterChain = new FilterChain();
        final FilterProcessor processor = mock(FilterProcessor.class);
        final String filteredMessage = "filtered message";
        final FilterProcessorResult result = filtered("message", filteredMessage, processor, "reason");
        filterChain.addProcessor(processor);

        when(processor.process(any(), any(), anyString())).thenReturn(result);

        // Act
        final FilterChainResult chainResult = filterChain.process(mock(ChatFilterPlayer.class), "test message");

        // Assert
        assertAll(
                () -> assertNotNull(chainResult, "Result is null"),
                () -> assertEquals(1, chainResult.getResults().size(), "Result size is not correct"),
                () -> assertTrue(chainResult.isFiltered(), "Message is not filtered"),
                () -> assertEquals(result, chainResult.getFilteredBy().get(), "Result is not correct"),
                () -> assertEquals(filteredMessage, chainResult.getFilteredMessage().get(), "Filtered message is not correct")
        );
    }

    @Test
    void testProcessWithAllowedMessage() {
        // Arrange
        final FilterChain filterChain = new FilterChain();
        final FilterProcessor processor = mock(FilterProcessor.class);
        final FilterProcessorResult result = allowed("message", processor, "reason");
        filterChain.addProcessor(processor);

        when(processor.process(any(), any(), anyString())).thenReturn(result);

        // Act
        final FilterChainResult chainResult = filterChain.process(mock(ChatFilterPlayer.class), "test message");

        // Assert
        assertAll(
                () -> assertNotNull(chainResult, "Result is null"),
                () -> assertEquals(1, chainResult.getResults().size(), "Result size is not correct"),
                () -> assertTrue(chainResult.isAllowed(), "Message is blocked")
        );
    }

    @Test
    void testProcessWithSpecificProcessor() throws FilterNotFoundException {
        // Arrange
        final FilterChain filterChain = new FilterChain();
        final FilterProcessor processor = mock(FilterProcessor.class);
        final FilterProcessorResult result = mock(FilterProcessorResult.class);
        filterChain.addProcessor(processor);

        when(processor.getName()).thenReturn("testProcessor");
        when(processor.process(any(), any(), anyString())).thenReturn(result);

        // Act
        final FilterChainResult chainResult = filterChain.process(null, "test message", "testProcessor");

        // Assert
        assertAll(
                () -> assertNotNull(chainResult, "Result is null"),
                () -> assertEquals(1, chainResult.getResults().size(), "Result size is not correct")
        );

    }

    @Test
    void testProcessWithNonExistentProcessor() {
        // Arrange
        final FilterChain filterChain = new FilterChain();

        // Act & Assert
        assertThrows(FilterNotFoundException.class, () -> filterChain.process(null, "test message", "nonExistentProcessor"));
    }

    @Test
    void testAddAndRemoveProcessor() {
        // Arrange
        final FilterChain filterChain = new FilterChain();
        final FilterProcessor processor = mock(FilterProcessor.class);
        when(processor.getPriority()).thenReturn(1);

        // Act & Assert
        filterChain.addProcessor(processor);
        assertEquals(1, filterChain.getProcessors().size(), "Processor was not added");

        filterChain.removeProcessor(processor);
        assertEquals(0, filterChain.getProcessors().size(), "Processor was not removed");
    }

    @Test
    void testProcessWithStopOnBlockImplicit() {
        // Arrange
        final FilterChain filterChain = new FilterChain();
        final FilterProcessor processor1 = mock(FilterProcessor.class);
        final FilterProcessor processor2 = mock(FilterProcessor.class);

        when(processor1.getPriority()).thenReturn(1);
        when(processor2.getPriority()).thenReturn(2);

        when(processor2.process(any(), any(), anyString())).thenReturn(blocked("message", processor1, "reason"));
        when(processor1.process(any(), any(), anyString())).thenReturn(allowed("message", processor2, "reason"));

        filterChain.addProcessor(processor1);
        filterChain.addProcessor(processor2);

        // Act
        final FilterChainResult result = filterChain.process(mock(ChatFilterPlayer.class), "test message");

        assertAll(
                () -> assertNotNull(result, "Result is null"),
                () -> assertEquals(1, result.getResults().size(), "Result size is not correct"),
                () -> assertTrue(result.isBlocked(), "Message is not blocked"),
                () -> assertEquals("reason", result.getBlockedReason().get(), "Blocked reason is not correct")
        );

    }

    @Test
    void testProcessWithStopOnBlockTrue() {
        // Arrange
        final FilterChain filterChain = new FilterChain();
        final FilterProcessor processor1 = mock(FilterProcessor.class);
        final FilterProcessor processor2 = mock(FilterProcessor.class);

        when(processor1.getPriority()).thenReturn(1);
        when(processor2.getPriority()).thenReturn(2);

        when(processor2.process(any(), any(), anyString())).thenReturn(blocked("message", processor1, "reason"));
        when(processor1.process(any(), any(), anyString())).thenReturn(allowed("message", processor2, "reason"));

        filterChain.addProcessor(processor1);
        filterChain.addProcessor(processor2);

        // Act
        final FilterChainResult result = filterChain.process(mock(ChatFilterPlayer.class), "test message", true);

        assertAll(
                () -> assertNotNull(result, "Result is null"),
                () -> assertEquals(1, result.getResults().size(), "Result size is not correct"),
                () -> assertTrue(result.isBlocked(), "Message is not blocked"),
                () -> assertEquals("reason", result.getBlockedReason().get(), "Blocked reason is not correct")
        );
    }

    @Test
    void testProcessWithStopOnBlockFalse() {
        // Arrange
        final FilterChain filterChain = new FilterChain();
        final FilterProcessor processor1 = mock(FilterProcessor.class);
        final FilterProcessor processor2 = mock(FilterProcessor.class);

        when(processor1.getPriority()).thenReturn(1);
        when(processor2.getPriority()).thenReturn(2);

        when(processor2.process(any(), any(), anyString())).thenReturn(blocked("message", processor1, "reason"));
        when(processor1.process(any(), any(), anyString())).thenReturn(allowed("message", processor2, "reason"));

        filterChain.addProcessor(processor1);
        filterChain.addProcessor(processor2);

        // Act
        final FilterChainResult result = filterChain.process(mock(ChatFilterPlayer.class), "test message", false);

        assertAll(
                () -> assertNotNull(result, "Result is null"),
                () -> assertEquals(2, result.getResults().size(), "Result size is not correct"),
                () -> assertTrue(result.isBlocked(), "Message is not blocked"),
                () -> assertEquals("reason", result.getBlockedReason().get(), "Blocked reason is not correct")
        );
    }

    @Test
    void testAddProcessorsWithPriority() {
        // Arrange
        final FilterChain filterChain = new FilterChain();
        final FilterProcessor processor1 = mock(FilterProcessor.class);
        final FilterProcessor processor2 = mock(FilterProcessor.class);
        final FilterProcessor processor3 = mock(FilterProcessor.class);

        when(processor1.getPriority()).thenReturn(1);
        when(processor2.getPriority()).thenReturn(2);
        when(processor3.getPriority()).thenReturn(3);

        // Act
        filterChain.addProcessor(processor2);
        filterChain.addProcessor(processor1);
        filterChain.addProcessor(processor3);

        // Assert
        assertAll(
                () -> assertEquals(3, filterChain.getProcessors().size(), "Processors were not added with correct priority"),
                () -> assertEquals(processor3, filterChain.getProcessors().get(0), "Processor 3 is not at the correct position"),
                () -> assertEquals(processor2, filterChain.getProcessors().get(1), "Processor 2 is not at the correct position"),
                () -> assertEquals(processor1, filterChain.getProcessors().get(2), "Processor 1 is not at the correct position")
        );
    }

    @Test
    void testLoadFilters() {
        // Arrange
        final FilterChain filterChain = new FilterChain();
        final ChatFilterConfig config = new ChatFilterConfig();
        config.setBlockedPatternFilterConfig(ChatFilterConfig.BlockedPatternFilterConfig.builder()
                .name("blockedPatternFilter")
                .patterns(List.of("pattern1", "pattern2"))
                .priority(1)
                .enabled(true)
                .build()
        );
        config.setLastMessageTimeFilterConfig(ChatFilterConfig.LastMessageTimeFilterConfig.builder()
                .name("lastMessageTimeFilter")
                .priority(2)
                .enabled(true)
                .build()
        );
        config.setMaxUpperCaseFilterConfig(ChatFilterConfig.MaxUpperCaseFilterConfig.builder()
                .name("maxUpperCaseFilter")
                .priority(3)
                .enabled(true)
                .build()
        );
        config.setSameMessageFilterConfig(ChatFilterConfig.SameMessageFilterConfig.builder()
                .name("sameMessageFilter")
                .priority(4)
                .enabled(true)
                .build()
        );
        config.setSimilarityFilterConfig(ChatFilterConfig.SimilarityFilterConfig.builder()
                .name("similarityFilter")
                .priority(5)
                .enabled(true)
                .build()
        );
        config.setTooManyViolationsFilterConfig(ChatFilterConfig.TooManyViolationsFilterConfig.builder()
                .name("tooManyViolationsFilter")
                .priority(6)
                .enabled(true)
                .build()
        );

        // Act
        filterChain.loadFilters(config);

        // Assert
        assertEquals(6, filterChain.getProcessors().size(), "Processors were not loaded correctly");

    }

    @Test
    void testLoadFiltersWithDisabled() {
        // Arrange
        final FilterChain filterChain = new FilterChain();
        final ChatFilterConfig config = new ChatFilterConfig();
        config.setBlockedPatternFilterConfig(ChatFilterConfig.BlockedPatternFilterConfig.builder()
                .name("blockedPatternFilter")
                .patterns(List.of("pattern1", "pattern2"))
                .priority(1)
                .enabled(true)
                .build()
        );
        config.setLastMessageTimeFilterConfig(ChatFilterConfig.LastMessageTimeFilterConfig.builder()
                .name("lastMessageTimeFilter")
                .priority(2)
                .enabled(true)
                .build()
        );
        config.setMaxUpperCaseFilterConfig(ChatFilterConfig.MaxUpperCaseFilterConfig.builder()
                .name("maxUpperCaseFilter")
                .priority(3)
                .enabled(true)
                .build()
        );
        config.setSameMessageFilterConfig(ChatFilterConfig.SameMessageFilterConfig.builder()
                .name("sameMessageFilter")
                .priority(4)
                .enabled(true)
                .build()
        );
        config.setSimilarityFilterConfig(ChatFilterConfig.SimilarityFilterConfig.builder()
                .name("similarityFilter")
                .priority(5)
                .enabled(false)
                .build()
        );
        config.setTooManyViolationsFilterConfig(ChatFilterConfig.TooManyViolationsFilterConfig.builder()
                .name("tooManyViolationsFilter")
                .priority(6)
                .enabled(true)
                .build()
        );

        // Act
        filterChain.loadFilters(config);

        // Assert
        assertEquals(5, filterChain.getProcessors().size(), "Processors were not loaded correctly");

    }
}

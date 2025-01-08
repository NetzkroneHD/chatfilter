package de.netzkronehd.chatfilter.stringcomparator.impl;

import de.netzkronehd.chatfilter.stringcomparator.StringComparator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LevenshteinDistanceTest {

    @Test
    void testSimilarity() {
        // Arrange
        final String lastMessage = "a".repeat(100);
        final String nextMessage = "a".repeat(81)+"b".repeat(19);
        final StringComparator stringComparator = new LevenshteinDistance();

        // Act
        final double similarity = stringComparator.getSimilarity(lastMessage, nextMessage);

        // Assert
        assertEquals(0.81, similarity, "Similarity is not correct");
    }

    @Test
    void testSimilarityWithEqualStrings() {
        // Arrange
        final String lastMessage = "a".repeat(100);
        final String nextMessage = "a".repeat(100);
        final StringComparator stringComparator = new LevenshteinDistance();

        // Act
        final double similarity = stringComparator.getSimilarity(lastMessage, nextMessage);

        // Assert
        assertEquals(1.0, similarity, "Similarity is not correct");
    }

    @Test
    void testSimilarityWithDifferentStrings() {
        // Arrange
        final String lastMessage = "a".repeat(100);
        final String nextMessage = "b".repeat(100);
        final StringComparator stringComparator = new LevenshteinDistance();

        // Act
        final double similarity = stringComparator.getSimilarity(lastMessage, nextMessage);

        // Assert
        assertEquals(0.0, similarity, "Similarity is not correct");
    }

    @Test
    void testSimilarityWithNullString() {
        // Arrange
        final String lastMessage = "a".repeat(100);
        final String nextMessage = null;
        final StringComparator stringComparator = new LevenshteinDistance();

        // Act
        final double similarity = stringComparator.getSimilarity(lastMessage, nextMessage);

        // Assert
        assertEquals(0.0, similarity, "Similarity is not correct");
    }

}

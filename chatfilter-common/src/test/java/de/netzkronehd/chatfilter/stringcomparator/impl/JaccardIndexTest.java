package de.netzkronehd.chatfilter.stringcomparator.impl;

import de.netzkronehd.chatfilter.stringcomparator.StringComparator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JaccardIndexTest {

    @Test
    void testSimilarityWithExactMatch() {
        // Arrange
        final String s1 = "hello";
        final String s2 = "hello";
        final StringComparator stringComparator = new JaccardIndex();

        // Act
        final double similarity = stringComparator.getSimilarity(s1, s2);

        // Assert
        assertEquals(1.0, similarity, "Similarity should be 1.0 for exact match");
    }

    @Test
    void testSimilarityWithPartialMatch() {
        // Arrange
        final String s1 = "hello";
        final String s2 = "hallo";
        final StringComparator stringComparator = new JaccardIndex();

        // Act
        final double similarity = stringComparator.getSimilarity(s1, s2);

        // Assert
        assertTrue(similarity > 0.0 && similarity < 1.0, "Similarity should be between 0.0 and 1.0 for partial match");
    }

    @Test
    void testSimilarityWithNoMatch() {
        // Arrange
        final String s1 = "hello";
        final String s2 = "asdfg";
        final StringComparator stringComparator = new JaccardIndex();

        // Act
        final double similarity = stringComparator.getSimilarity(s1, s2);

        // Assert
        assertEquals(0.0, similarity, "Similarity should be 0.0 for no match");
    }

    @Test
    void testSimilarityWithEmptyStrings() {
        // Arrange
        final String s1 = "";
        final String s2 = "";
        final StringComparator stringComparator = new JaccardIndex();

        // Act
        final double similarity = stringComparator.getSimilarity(s1, s2);

        // Assert
        assertEquals(1.0, similarity, "Similarity should be 1.0 for empty strings");
    }

    @Test
    void testSimilarityWithNullString() {
        // Arrange
        final String s1 = "hello";
        final StringComparator stringComparator = new JaccardIndex();

        // Act
        final double similarity = stringComparator.getSimilarity(s1, null);

        // Assert
        assertEquals(0.0, similarity, "Similarity should be 0.0 when one string is empty");
    }
}

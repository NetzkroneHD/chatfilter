package de.netzkronehd.chatfilter.stringcomparator.impl;

import de.netzkronehd.chatfilter.stringcomparator.StringComparator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CosineSimilarityTest {

    @Test
    void testSimilarityWithExactMatch() {
        // Arrange
        final String s1 = "hello";
        final String s2 = "hello";
        final StringComparator stringComparator = new CosineSimilarity();

        // Act
        final double similarity = stringComparator.getSimilarity(s1, s2);

        // Assert
        assertEquals(1.0, similarity, "Similarity should be 1.0 for exact match");
    }

    @Test
    void testSimilarityWithPartialMatch() {
        // Arrange
        final String s1 = "abcde";
        final String s2 = "abcef";
        final StringComparator stringComparator = new CosineSimilarity();

        // Act
        final double similarity = stringComparator.getSimilarity(s1, s2);

        // Assert

        assertEquals(0.8, similarity, 1e-9, "Similarity should be 0.8 for partial match");
    }

    @Test
    void testSimilarityWithNoMatch() {
        // Arrange
        final String s1 = "hello";
        final String s2 = "asdfg";
        final StringComparator stringComparator = new CosineSimilarity();

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
        final StringComparator stringComparator = new CosineSimilarity();

        // Act
        final double similarity = stringComparator.getSimilarity(s1, s2);

        // Assert
        assertEquals(1.0, similarity, "Similarity should be 1.0 for empty strings");
    }

    @Test
    void testSimilarityWithNullString() {
        // Arrange
        final String s1 = "hello";
        final StringComparator stringComparator = new CosineSimilarity();

        // Act
        final double similarity = stringComparator.getSimilarity(s1, null);

        // Assert
        assertEquals(0.0, similarity, "Similarity should be 0.0 when one string is empty");
    }
}

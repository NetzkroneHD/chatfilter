package de.netzkronehd.chatfilter.stringcomparator;

import de.netzkronehd.chatfilter.stringcomparator.impl.CosineSimilarity;
import de.netzkronehd.chatfilter.stringcomparator.impl.JaccardIndex;
import de.netzkronehd.chatfilter.stringcomparator.impl.LevenshteinDistance;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringComparatorTest {

    @Test
    void testGetSimilarityWithExactMatch() {
        // Arrange
        final String s1 = "Hello";
        final String s2 = "hello";
        final StringComparator stringComparator = new TestStringComparator();

        // Act
        final double similarity = stringComparator.getSimilarity(s1, s2);

        // Assert
        assertEquals(1.0, similarity, "Similarity should be 1.0 for exact match");
    }

    @Test
    void testGetSimilarityWithExactMatchFromLevenshteinDistance() {
        // Arrange
        final String s1 = "Hello";
        final String s2 = "hello";
        final StringComparator stringComparator = new TestLevenshteinDistance();

        // Act
        final double similarity = stringComparator.getSimilarity(s1, s2);

        // Assert
        assertEquals(1.0, similarity, "Similarity should be 1.0 for exact match");
    }

    @Test
    void testGetSimilarityWithExactMatchFromJaccardIndex() {
        // Arrange
        final String s1 = "Hello";
        final String s2 = "hello";
        final StringComparator stringComparator = new TestJaccardIndex();

        // Act
        final double similarity = stringComparator.getSimilarity(s1, s2);

        // Assert
        assertEquals(1.0, similarity, "Similarity should be 1.0 for exact match");
    }

    @Test
    void testGetSimilarityWithExactMatchFromCosineSimilarity() {
        // Arrange
        final String s1 = "Hello";
        final String s2 = "hello";
        final StringComparator stringComparator = new TestCosineSimilarity();

        // Act
        final double similarity = stringComparator.getSimilarity(s1, s2);

        // Assert
        assertEquals(1.0, similarity, "Similarity should be 1.0 for exact match");
    }


    @Test
    void testGetSimilarityWithNullString() {
        // Arrange
        final String s1 = "hello";
        final StringComparator stringComparator = new TestStringComparator();

        // Act
        final double similarity = stringComparator.getSimilarity(s1, null);

        // Assert
        assertEquals(0.0, similarity, "Similarity should be 0.0 when one string is null");
    }

    @Test
    void testGetSimilarityWithNullStrings() {
        // Arrange
        final StringComparator stringComparator = new TestStringComparator();

        // Act
        final double similarity = stringComparator.getSimilarity(null, null);

        // Assert
        assertEquals(0.0, similarity, "Similarity should be 0.0 when both strings are null");
    }


    static class TestStringComparator implements StringComparator {

        @Override
        public double similarity(String s1, String s2) {
            throw new UnsupportedOperationException("Should not be called");
        }
    }

    static class TestLevenshteinDistance extends LevenshteinDistance {

        @Override
        public double similarity(String s1, String s2) {
            throw new UnsupportedOperationException("Should not be called");
        }
    }

    static class TestCosineSimilarity extends CosineSimilarity {

        @Override
        public double similarity(String s1, String s2) {
            throw new UnsupportedOperationException("Should not be called");
        }
    }

    static class TestJaccardIndex extends JaccardIndex {

        @Override
        public double similarity(String s1, String s2) {
            throw new UnsupportedOperationException("Should not be called");
        }
    }

}

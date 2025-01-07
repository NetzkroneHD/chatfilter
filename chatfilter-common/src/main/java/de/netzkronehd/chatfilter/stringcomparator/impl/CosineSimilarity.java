package de.netzkronehd.chatfilter.stringcomparator.impl;

import de.netzkronehd.chatfilter.stringcomparator.StringComparator;

import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of the Cosine similarity to calculate the similarity between two strings.
 * See <a href="https://en.wikipedia.org/wiki/Cosine_similarity">Cosine similarity</a> for more information.
 */
public class CosineSimilarity implements StringComparator {

    @Override
    public double similarity(String s1, String s2) {
        return calculateCosineSimilarity(s1, s2);
    }

    private double calculateCosineSimilarity(String s1, String s2) {
        final Map<Character, int[]> vectorMap = new HashMap<>();

        for (char c : s1.toCharArray()) {
            vectorMap.putIfAbsent(c, new int[2]);
            vectorMap.get(c)[0]++;
        }

        for (char c : s2.toCharArray()) {
            vectorMap.putIfAbsent(c, new int[2]);
            vectorMap.get(c)[1]++;
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int[] counts : vectorMap.values()) {
            dotProduct += counts[0] * counts[1];
            normA += Math.pow(counts[0], 2);
            normB += Math.pow(counts[1], 2);
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

}

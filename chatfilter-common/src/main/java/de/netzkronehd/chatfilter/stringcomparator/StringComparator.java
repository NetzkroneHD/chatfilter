package de.netzkronehd.chatfilter.stringcomparator;

/**
 * A comparator to calculate the similarity between two strings.
 * The similarity is a value between 0.0 and 1.0 where 1.0 means the strings are equal.
 */
public interface StringComparator {

    /**
     * Calculates the similarity between two strings.
     * This method checks for null values and returns 0.0 if one of the strings is null.
     * If both strings are equal, 1.0 is returned.
     * delegates to {@link #similarity(String, String)} so it should be overridden by implementations.
     * @param s1 the first string
     * @param s2 the second string
     * @return the similarity between the two strings as a value between 0.0 and 1.0
     */
    default double getSimilarity(String s1, String s2) {
        if (s1 == null) return 0;
        if (s2 == null) return 0;
        if (s1.equalsIgnoreCase(s2)) return 1.0;

        return similarity(s1, s2);
    }

    /**
     * Calculates the similarity between two strings.
     * @param s1 the first string
     * @param s2 the second string
     * @return the similarity between the two strings as a value between 0.0 and 1.0
     */
    double similarity(String s1, String s2);

}

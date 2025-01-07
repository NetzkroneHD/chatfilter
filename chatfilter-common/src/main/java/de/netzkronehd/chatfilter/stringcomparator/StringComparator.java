package de.netzkronehd.chatfilter.stringcomparator;

/**
 * A comparator to calculate the similarity between two strings.
 * The similarity is a value between 0.0 and 1.0 where 1.0 means the strings are equal.
 */
public interface StringComparator {

    default double getSimilarity(String s1, String s2) {
        if (s1 == null) return 0;
        if (s2 == null) return 0;
        if (s1.equalsIgnoreCase(s2)) return 1.0;

        return similarity(s1, s2);
    }

    double similarity(String s1, String s2);

}

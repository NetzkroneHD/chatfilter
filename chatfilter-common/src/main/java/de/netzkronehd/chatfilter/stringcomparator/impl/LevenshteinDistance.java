package de.netzkronehd.chatfilter.stringcomparator.impl;

import de.netzkronehd.chatfilter.stringcomparator.StringComparator;

/**
 * An implementation of the Levenshtein distance to calculate the similarity between two strings.
 * See <a href="https://en.wikipedia.org/wiki/Levenshtein_distance">Levenshtein distance</a> for more information.
 */
public class LevenshteinDistance implements StringComparator {

    @Override
    public double similarity(String s1, String s2) {
        final double maxLength = Integer.max(s1.length(), s2.length());
        if (maxLength > 0) {
            return (maxLength - calculateLevenshteinDistance(s1, s2)) / maxLength;
        }
        return 1.0;
    }

    private int calculateLevenshteinDistance(CharSequence left, CharSequence right) {
        int n = left.length();
        int m = right.length();

        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }

        if (n > m) {
            final CharSequence tmp = left;
            left = right;
            right = tmp;
            n = m;
            m = right.length();
        }

        final int[] p = new int[n + 1];

        int i;
        int j;
        int upperLeft;
        int upper;

        char rightJ;
        int cost;

        for (i = 0; i <= n; i++) {
            p[i] = i;
        }

        for (j = 1; j <= m; j++) {
            upperLeft = p[0];
            rightJ = right.charAt(j - 1);
            p[0] = j;

            for (i = 1; i <= n; i++) {
                upper = p[i];
                cost = left.charAt(i - 1) == rightJ ? 0 : 1;
                p[i] = Math.min(Math.min(p[i - 1] + 1, p[i] + 1), upperLeft + cost);
                upperLeft = upper;
            }
        }

        return p[n];
    }

}

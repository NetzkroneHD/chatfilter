package de.netzkronehd.chatfilter.stringcomparator.impl;

import de.netzkronehd.chatfilter.stringcomparator.StringComparator;

import java.util.HashSet;
import java.util.Set;

/**
 * An implementation of the Jaccard index to calculate the similarity between two strings.
 * See <a href="https://en.wikipedia.org/wiki/Jaccard_index">Jaccard index</a> for more information.
 */
public class JaccardIndex implements StringComparator {

    @Override
    public double similarity(String s1, String s2) {
        return calculateJaccardIndex(s1, s2);
    }

    private double calculateJaccardIndex(String s1, String s2) {
        final Set<Character> set1 = new HashSet<>();
        final Set<Character> set2 = new HashSet<>();

        for (char c : s1.toCharArray()) {
            set1.add(c);
        }

        for (char c : s2.toCharArray()) {
            set2.add(c);
        }

        final Set<Character> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        final Set<Character> union = new HashSet<>(set1);
        union.addAll(set2);

        return (double) intersection.size() / union.size();
    }

}

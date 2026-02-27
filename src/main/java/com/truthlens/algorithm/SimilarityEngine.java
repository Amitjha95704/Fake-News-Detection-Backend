package com.truthlens.algorithm;

import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.Set;

@Component
public class SimilarityEngine {

    // Calculates similarity percentage (0.0 to 100.0) between two sets of keywords
    public double calculateJaccardSimilarity(Set<String> keywords1, Set<String> keywords2) {
        if (keywords1.isEmpty() || keywords2.isEmpty()) return 0.0;

        Set<String> intersection = new HashSet<>(keywords1);
        intersection.retainAll(keywords2); // Keep only common words

        Set<String> union = new HashSet<>(keywords1);
        union.addAll(keywords2); // Combine all words

        return ((double) intersection.size() / union.size()) * 100.0;
    }
}
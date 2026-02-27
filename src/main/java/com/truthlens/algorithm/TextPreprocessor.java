package com.truthlens.algorithm;

import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TextPreprocessor {

    // Common stop words in English
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "a", "an", "the", "and", "or", "but", "is", "are", "am", "was", "were", 
            "be", "been", "to", "of", "in", "for", "on", "with", "as", "by", "at", 
            "this", "that", "it", "he", "she", "they", "we", "you", "i", "my", "has", "have"
    ));

    public Set<String> extractKeywords(String text) {
        if (text == null || text.trim().isEmpty()) return new HashSet<>();

        // Remove punctuation and convert to lowercase
        String cleanText = text.replaceAll("[^a-zA-Z0-9\\s]", "").toLowerCase();

        // Split into words, filter out stop words, and return as a Set
        return Arrays.stream(cleanText.split("\\s+"))
                .filter(word -> !STOP_WORDS.contains(word) && word.length() > 2)
                .collect(Collectors.toSet());
    }
}
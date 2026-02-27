//package com.truthlens.algorithm;
//
//import com.truthlens.dto.response.EvidenceDto;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//public class LocalScoringEngine {
//
//    // Yeh wo words hain jo prove karte hain ki koi claim galat/fake hai
//    private static final List<String> CONTRADICT_WORDS = List.of(
//            "hoax", "fake", "false", "debunked", "rumor", "denies", "clarifies", 
//            "misleading", "untrue", "busted", "scam", "fact check"
//    );
//
//    public ScoringResult calculate(String claim, List<EvidenceDto> scrapedNews) {
//        if (scrapedNews == null || scrapedNews.isEmpty()) {
//            return new ScoringResult("UNVERIFIABLE", 15, 0, 0);
//        }
//
//        int supportCount = 0;
//        int contradictCount = 0;
//
//        // Har headline ko check karo
//        for (EvidenceDto evidence : scrapedNews) {
//            String headline = evidence.getTitle().toLowerCase();
//            boolean isContradiction = false;
//
//            for (String word : CONTRADICT_WORDS) {
//                if (headline.contains(word)) {
//                    isContradiction = true;
//                    break;
//                }
//            }
//
//            // Headline ke hisaab se Stance set karo (UI ke liye achha dikhega)
//            if (isContradiction) {
//                contradictCount++;
//                evidence.setStance("CONTRADICTS");
//            } else {
//                supportCount++;
//                evidence.setStance("SUPPORTS");
//            }
//        }
//
//        int total = scrapedNews.size();
//        String verdict;
//        int score;
//
//        // Custom Math Logic for Score
//        if (contradictCount > supportCount) {
//            verdict = "FALSE";
//            score = 60 + (int) (((double) contradictCount / total) * 40);
//        } else if (supportCount > contradictCount) {
//            verdict = "TRUE";
//            score = 60 + (int) (((double) supportCount / total) * 40);
//        } else {
//            verdict = "MIXED";
//            score = 50;
//        }
//
//        // Confidence Snapping (Agar clear cut hai toh 100% kar do)
//        if (score >= 90) score = 100;
//
//        return new ScoringResult(verdict, score, supportCount, contradictCount);
//    }
//
//    // Ek chota sa inner class data return karne ke liye
//    public static class ScoringResult {
//        private String verdict;
//        private int score;
//        private int supports;
//        private int contradicts;
//
//        public ScoringResult(String verdict, int score, int supports, int contradicts) {
//            this.verdict = verdict;
//            this.score = score;
//            this.supports = supports;
//            this.contradicts = contradicts;
//        }
//
//        public String getVerdict() { return verdict; }
//        public int getScore() { return score; }
//        public int getSupports() { return supports; }
//        public int getContradicts() { return contradicts; }
//    }
//}

package com.truthlens.algorithm;

import com.truthlens.dto.response.EvidenceDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class LocalScoringEngine {

    private static final List<String> CONTRADICT_WORDS = List.of(
            "hoax", "fake", "false", "debunked", "rumor", "denies", "clarifies", 
            "misleading", "untrue", "busted", "scam", "fact check"
    );

    // 🔥 NAYA UPDATE: Ab hum userKeywords bhi le rahe hain filter karne ke liye
    public ScoringResult calculate(String claim, List<EvidenceDto> scrapedNews, Set<String> userKeywords) {
        if (scrapedNews == null || scrapedNews.isEmpty()) {
            return new ScoringResult("UNVERIFIABLE", 15, 0, 0);
        }

        int supportCount = 0;
        int contradictCount = 0;
        int relevantArticles = 0;

        for (EvidenceDto evidence : scrapedNews) {
            String headline = evidence.getTitle().toLowerCase();

            // ==========================================
            // SMART FILTER: RELEVANCE CHECK
            // ==========================================
            int keywordMatches = 0;
            for (String kw : userKeywords) {
                if (headline.contains(kw.toLowerCase())) {
                    keywordMatches++;
                }
            }

            // Agar headline mein hamara ek bhi main keyword nahi hai (jaise "AI" ya "Summit"), 
            // toh yeh kachra hai, isko ignore karo!
            if (keywordMatches == 0 && !userKeywords.isEmpty()) {
                evidence.setStance("IRRELEVANT");
                continue; // Is article ko skip kar do
            }

            relevantArticles++;

            // ==========================================
            // CONTRADICTION CHECK (Only for relevant news)
            // ==========================================
            boolean isContradiction = false;
            for (String word : CONTRADICT_WORDS) {
                if (headline.contains(word)) {
                    isContradiction = true;
                    break;
                }
            }

            if (isContradiction) {
                contradictCount++;
                evidence.setStance("CONTRADICTS");
            } else {
                supportCount++;
                evidence.setStance("SUPPORTS");
            }
        }

        // Agar saari news irrelevant nikli (jaise CA Exams ki news for AI claim)
        if (relevantArticles == 0) {
            return new ScoringResult("UNVERIFIABLE", 15, 0, 0);
        }

        String verdict;
        int score;

        // Sirf un articles par math lagao jo kaam ke hain
        if (contradictCount > supportCount) {
            verdict = "FALSE";
            score = 60 + (int) (((double) contradictCount / relevantArticles) * 35); // Max 95%
        } else if (supportCount > contradictCount) {
            verdict = "LIKELY TRUE"; 
            // Local engine ko bina AI ke kabhi 100% nahi dena chahiye, max 85% safe hai
            score = 50 + (int) (((double) supportCount / relevantArticles) * 35); 
        } else {
            verdict = "MIXED / RECENT NEWS";
            score = 50;
        }

        return new ScoringResult(verdict, score, supportCount, contradictCount);
    }

    public static class ScoringResult {
        private String verdict;
        private int score;
        private int supports;
        private int contradicts;

        public ScoringResult(String verdict, int score, int supports, int contradicts) {
            this.verdict = verdict;
            this.score = score;
            this.supports = supports;
            this.contradicts = contradicts;
        }

        public String getVerdict() { return verdict; }
        public int getScore() { return score; }
        public int getSupports() { return supports; }
        public int getContradicts() { return contradicts; }
    }
}
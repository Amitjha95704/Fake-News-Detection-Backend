
//////package com.truthlens.service;
//////
//////import com.truthlens.algorithm.SimilarityEngine;
//////import com.truthlens.algorithm.TextPreprocessor;
//////import com.truthlens.dto.request.VerificationRequest;
//////import com.truthlens.dto.response.ClaimDto;
//////import com.truthlens.dto.response.EvidenceDto;
//////import com.truthlens.dto.response.VerificationResponse;
//////import com.truthlens.entity.FactCheckRecord;
//////import com.truthlens.repository.FactCheckRepository;
//////import com.truthlens.scraper.NewsScraperService;
//////import lombok.RequiredArgsConstructor;
//////import org.springframework.stereotype.Service;
//////
//////import java.util.ArrayList;
//////import java.util.List;
//////import java.util.Map;
//////import java.util.Set;
//////
//////@Service
//////@RequiredArgsConstructor
//////public class VerificationService {
//////
//////    private final TextPreprocessor preprocessor;
//////    private final SimilarityEngine similarityEngine;
//////    private final FactCheckRepository dbRepository;
//////    private final NewsScraperService scraperService;
//////    
//////    // 💡 NEW INJECTION: Hamara Gemini AI Service
//////    private final AiAnalysisService aiAnalysisService;
//////
//////    public VerificationResponse process(VerificationRequest request) {
//////        String userInput = request.getContent();
//////        Set<String> userKeywords = preprocessor.extractKeywords(userInput);
//////        
//////        List<EvidenceDto> allEvidence = new ArrayList<>();
//////        int highestMatchScore = 0;
//////        String finalVerdict = "UNVERIFIED";
//////        String finalExplanation = "";
//////
//////        // ==========================================
//////        // STEP 1: Check Local MySQL (Kaggle Dataset)
//////        // ==========================================
//////        String primaryKeyword = userKeywords.stream().findFirst().orElse("");
//////        List<FactCheckRecord> dbMatches = dbRepository.findByClaimTextContainingIgnoreCase(primaryKeyword);
//////        
//////        for (FactCheckRecord record : dbMatches) {
//////            Set<String> dbKeywords = preprocessor.extractKeywords(record.getClaimText());
//////            double score = similarityEngine.calculateJaccardSimilarity(userKeywords, dbKeywords);
//////            
//////            if (score > highestMatchScore) {
//////                highestMatchScore = (int) score;
//////                finalVerdict = record.getVerdict(); 
//////            }
//////            
//////            if (score > 30.0) { 
//////                allEvidence.add(EvidenceDto.builder()
//////                        .sourceName("TruthLens Core DB")
//////                        .title(record.getClaimText())
//////                        .url("#") 
//////                        .stance(record.getVerdict().toUpperCase().contains("TRUE") ? "SUPPORTS" : "CONTRADICTS")
//////                        .build());
//////            }
//////        }
//////
//////        // ==========================================
//////        // STEP 2: ALWAYS Scrape Live News (Jsoup)
//////        // ==========================================
//////        List<String> topKeywords = userKeywords.stream().limit(3).toList();
//////        String searchQuery = String.join("+", topKeywords);
//////        
//////        List<EvidenceDto> scrapedNews = scraperService.scrapeNews(searchQuery);
//////        allEvidence.addAll(scrapedNews);
//////
//////        // ==========================================
//////        // STEP 3: The Brain (AI Decision Making)
//////        // ==========================================
//////        
//////        // Agar local DB mein bohot strong match hai (> 60%), toh DB pe bharosa karo
//////        if (highestMatchScore > 60) {
//////            finalExplanation = "We found a strong match in our historical fact-check database.";
//////        } 
//////        // Agar DB match nahi mila, par internet se Live News mil gayi, toh call Gemini AI!
//////        else if (!scrapedNews.isEmpty()) {
//////            try {
//////                // Sending scraped news to AI Brain
//////                Map<String, Object> aiResult = aiAnalysisService.analyzeClaim(userInput, scrapedNews);
//////                
//////                finalVerdict = (String) aiResult.get("verdict");
//////                highestMatchScore = (Integer) aiResult.get("confidenceScore");
//////                finalExplanation = (String) aiResult.get("explanation");
//////            } catch (Exception e) {
//////                finalVerdict = "NEUTRAL / RECENT NEWS";
//////                highestMatchScore = 50;
//////                finalExplanation = "We scraped live news, but the AI Brain failed to process it. Error: " + e.getMessage();
//////            }
//////        } 
//////        // Ultimate Fallback
//////        else {
//////            finalVerdict = "UNVERIFIABLE";
//////            highestMatchScore = 15;
//////            finalExplanation = "TruthLens algorithms and web scrapers could not find sufficient evidence on the internet or local database.";
//////        }
//////
//////        // ==========================================
//////        // STEP 4: Package everything using Lombok Builder
//////        // ==========================================
//////        ClaimDto claimDto = ClaimDto.builder()
//////                .claimText(userInput)
//////                .verdict(finalVerdict)
//////                .evidence(allEvidence)
//////                .build();
//////
//////        return VerificationResponse.builder()
//////                .verdict(finalVerdict)
//////                .confidenceScore(highestMatchScore)
//////                .explanation(finalExplanation)
//////                .claims(List.of(claimDto))
//////                .build();
//////    }
//////}
////
////package com.truthlens.service;
////
////import com.truthlens.algorithm.SimilarityEngine;
////import com.truthlens.algorithm.TextPreprocessor;
////import com.truthlens.dto.request.VerificationRequest;
////import com.truthlens.dto.response.ClaimDto;
////import com.truthlens.dto.response.EvidenceDto;
////import com.truthlens.dto.response.VerificationResponse;
////import com.truthlens.entity.FactCheckRecord;
////import com.truthlens.repository.FactCheckRepository;
////import com.truthlens.scraper.NewsScraperService;
////import lombok.RequiredArgsConstructor;
////import org.springframework.stereotype.Service;
////
////import java.util.ArrayList;
////import java.util.List;
////import java.util.Map;
////import java.util.Set;
////
////@Service
////@RequiredArgsConstructor
////public class VerificationService {
////
////    private final TextPreprocessor preprocessor;
////    private final SimilarityEngine similarityEngine;
////    private final FactCheckRepository dbRepository;
////    private final NewsScraperService scraperService;
////    private final AiAnalysisService aiAnalysisService;
////
////    public VerificationResponse process(VerificationRequest request) {
////        String userInput = request.getContent();
////        Set<String> userKeywords = preprocessor.extractKeywords(userInput);
////        
////        List<EvidenceDto> allEvidence = new ArrayList<>();
////        int highestMatchScore = 0;
////        String finalVerdict = "UNVERIFIED";
////        String finalExplanation = "";
////
////        // ==========================================
////        // STEP 1: Check Local MySQL Database
////        // ==========================================
////        String primaryKeyword = userKeywords.stream().findFirst().orElse("");
////        List<FactCheckRecord> dbMatches = dbRepository.findByClaimTextContainingIgnoreCase(primaryKeyword);
////        
////        for (FactCheckRecord record : dbMatches) {
////            Set<String> dbKeywords = preprocessor.extractKeywords(record.getClaimText());
////            double score = similarityEngine.calculateJaccardSimilarity(userKeywords, dbKeywords);
////            
////            if (score > highestMatchScore) {
////                highestMatchScore = (int) score;
////                finalVerdict = record.getVerdict(); 
////            }
////            
////            if (score > 30.0) { 
////                allEvidence.add(EvidenceDto.builder()
////                        .sourceName("TruthLens Core DB")
////                        .title(record.getClaimText())
////                        .url("#") 
////                        .stance(record.getVerdict().toUpperCase().contains("TRUE") ? "SUPPORTS" : "CONTRADICTS")
////                        .build());
////            }
////        }
////
////        // ==========================================
////        // STEP 2: Scrape Live News (Jsoup)
////        // ==========================================
////        List<String> topKeywords = userKeywords.stream().limit(3).toList();
////        String searchQuery = String.join("+", topKeywords);
////        
////        List<EvidenceDto> scrapedNews = scraperService.scrapeNews(searchQuery);
////        allEvidence.addAll(scrapedNews);
////
////        // ==========================================
////        // STEP 3: The Brain (AI Decision Making) & Confidence Snapping
////        // ==========================================
////        
////        // Agar local DB mein bohot strong match hai (> 60%), toh DB pe bharosa karo
////        if (highestMatchScore > 60) {
////            // CONFIDENCE SNAPPING FOR DB
////            if (highestMatchScore >= 85) highestMatchScore = 100;
////            
////            // Format DB verdict nicely
////            if (finalVerdict.contains("TRUE")) finalVerdict = "TRUE";
////            if (finalVerdict.contains("FALSE")) finalVerdict = "FALSE";
////            
////            finalExplanation = "We found a strong match in our historical fact-check database.";
////        } 
////        // Agar DB match nahi mila, par internet se Live News mil gayi, toh call Gemini AI!
////        else if (!scrapedNews.isEmpty()) {
////            try {
////                Map<String, Object> aiResult = aiAnalysisService.analyzeClaim(userInput, scrapedNews);
////                
////                finalVerdict = (String) aiResult.get("verdict");
////                highestMatchScore = (Integer) aiResult.get("confidenceScore");
////                finalExplanation = (String) aiResult.get("explanation");
////                
////                // CONFIDENCE SNAPPING FOR AI
////                if (highestMatchScore >= 90) highestMatchScore = 100;
////
////            } catch (Exception e) {
////                finalVerdict = "NEUTRAL / RECENT NEWS";
////                highestMatchScore = 50;
////                finalExplanation = "We scraped live news, but the AI Brain failed to process it. Error: " + e.getMessage();
////            }
////        } 
////        // Ultimate Fallback
////        else {
////            finalVerdict = "UNVERIFIABLE";
////            highestMatchScore = 15;
////            finalExplanation = "TruthLens algorithms and web scrapers could not find sufficient evidence on the internet or local database.";
////        }
////
////        // ==========================================
////        // STEP 4: Package everything using Lombok Builder
////        // ==========================================
////        ClaimDto claimDto = ClaimDto.builder()
////                .claimText(userInput)
////                .verdict(finalVerdict)
////                .evidence(allEvidence)
////                .build();
////
////        return VerificationResponse.builder()
////                .verdict(finalVerdict)
////                .confidenceScore(highestMatchScore)
////                .explanation(finalExplanation)
////                .claims(List.of(claimDto))
////                .build();
////    }
////}
//
//package com.truthlens.service;
//
//import com.truthlens.algorithm.LocalScoringEngine;
//import com.truthlens.algorithm.SimilarityEngine;
//import com.truthlens.algorithm.TextPreprocessor;
//import com.truthlens.dto.request.VerificationRequest;
//import com.truthlens.dto.response.ClaimDto;
//import com.truthlens.dto.response.EvidenceDto;
//import com.truthlens.dto.response.VerificationResponse;
//import com.truthlens.entity.FactCheckRecord;
//import com.truthlens.repository.FactCheckRepository;
//import com.truthlens.scraper.NewsScraperService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//@Service
//@RequiredArgsConstructor
//public class VerificationService {
//
//    private final TextPreprocessor preprocessor;
//    private final SimilarityEngine similarityEngine;
//    private final FactCheckRepository dbRepository;
//    private final NewsScraperService scraperService;
//    private final AiAnalysisService aiAnalysisService;
//    private final LocalScoringEngine localScoringEngine;
//
//    public VerificationResponse process(VerificationRequest request) {
//        String userInput = request.getContent();
//        Set<String> userKeywords = preprocessor.extractKeywords(userInput);
//        
//        List<EvidenceDto> allEvidence = new ArrayList<>();
//        int highestMatchScore = 0;
//        String finalVerdict = "UNVERIFIED";
//        String finalExplanation = "";
//
//        // ==========================================
//        // STEP 1: Check Local MySQL Database
//        // ==========================================
//        String primaryKeyword = userKeywords.stream().findFirst().orElse("");
//        List<FactCheckRecord> dbMatches = dbRepository.findByClaimTextContainingIgnoreCase(primaryKeyword);
//        
//        for (FactCheckRecord record : dbMatches) {
//            Set<String> dbKeywords = preprocessor.extractKeywords(record.getClaimText());
//            double score = similarityEngine.calculateJaccardSimilarity(userKeywords, dbKeywords);
//            
//            if (score > highestMatchScore) {
//                highestMatchScore = (int) score;
//                finalVerdict = record.getVerdict(); 
//            }
//            
//            if (score > 30.0) { 
//                allEvidence.add(EvidenceDto.builder()
//                        .sourceName("TruthLens Core DB")
//                        .title(record.getClaimText())
//                        .url("#") 
//                        .stance(record.getVerdict().toUpperCase().contains("TRUE") ? "SUPPORTS" : "CONTRADICTS")
//                        .build());
//            }
//        }
//
//        // ==========================================
//        // STEP 2: Scrape Live News (Jsoup)
//        // ==========================================
//        List<String> topKeywords = userKeywords.stream().limit(3).toList();
//        String searchQuery = String.join("+", topKeywords);
//        
//        List<EvidenceDto> scrapedNews = scraperService.scrapeNews(searchQuery);
//        allEvidence.addAll(scrapedNews);
//
//        // ==========================================
//        // STEP 3: The Brain (AI) & Fault-Tolerant Backup
//        // ==========================================
//        
//        // Condition A: Local DB Match
//        if (highestMatchScore > 60) {
//            if (highestMatchScore >= 85) highestMatchScore = 100; // Snapping
//            
//            if (finalVerdict.contains("TRUE")) finalVerdict = "TRUE";
//            if (finalVerdict.contains("FALSE")) finalVerdict = "FALSE";
//            
//            finalExplanation = "We found a strong match in our historical fact-check database.";
//        } 
//        
//        // Condition B: No DB Match, but Live News Found
//        else if (!scrapedNews.isEmpty()) {
//            try {
//                // Pehle Gemini AI ko try karo
//                Map<String, Object> aiResult = aiAnalysisService.analyzeClaim(userInput, scrapedNews);
//                
//                finalVerdict = (String) aiResult.get("verdict");
//                highestMatchScore = (Integer) aiResult.get("confidenceScore");
//                finalExplanation = (String) aiResult.get("explanation");
//                
//                if (highestMatchScore >= 90) highestMatchScore = 100; // AI Snapping
//
//            } catch (Exception e) {
//                // 🔥 THE FALLBACK: AI limit exceed ya network error hone par
//                System.out.println("⚠️ Gemini API failed. Seamlessly switching to Local Scoring Engine! Error: " + e.getMessage());
//                
//                LocalScoringEngine.ScoringResult localResult = localScoringEngine.calculate(userInput, scrapedNews);
//                
//                finalVerdict = localResult.getVerdict();
//                highestMatchScore = localResult.getScore();
//                
//                // CLEAN USER-FRIENDLY MESSAGE
//                finalExplanation = "⚠️ AI API limit exceeded. Please try again after some time. " +
//                                   "(Local Algorithm Output: We found " + localResult.getSupports() + 
//                                   " supporting and " + localResult.getContradicts() + " contradicting sources.)";
//            }
//        } 
//        
//        // Condition C: Ultimate Fallback (Nothing found anywhere)
//        else {
//            finalVerdict = "UNVERIFIABLE";
//            highestMatchScore = 0;
//            finalExplanation = "TruthLens algorithms and web scrapers could not find sufficient evidence on the internet or local database.";
//        }
//
//        // ==========================================
//        // STEP 4: Package everything using Lombok Builder
//        // ==========================================
//        ClaimDto claimDto = ClaimDto.builder()
//                .claimText(userInput)
//                .verdict(finalVerdict)
//                .evidence(allEvidence)
//                .build();
//
//        return VerificationResponse.builder()
//                .verdict(finalVerdict)
//                .confidenceScore(highestMatchScore)
//                .explanation(finalExplanation)
//                .claims(List.of(claimDto))
//                .build();
//    }
//}

package com.truthlens.service;

import com.truthlens.algorithm.LocalScoringEngine;
import com.truthlens.algorithm.SimilarityEngine;
import com.truthlens.algorithm.TextPreprocessor;
import com.truthlens.dto.request.VerificationRequest;
import com.truthlens.dto.response.ClaimDto;
import com.truthlens.dto.response.EvidenceDto;
import com.truthlens.dto.response.VerificationResponse;
import com.truthlens.entity.FactCheckRecord;
import com.truthlens.repository.FactCheckRepository;
import com.truthlens.scraper.NewsScraperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final TextPreprocessor preprocessor;
    private final SimilarityEngine similarityEngine;
    private final FactCheckRepository dbRepository;
    private final NewsScraperService scraperService;
    private final AiAnalysisService aiAnalysisService;
    private final LocalScoringEngine localScoringEngine;

    public VerificationResponse process(VerificationRequest request) {
        String userInput = request.getContent();
        
        // Extract keywords from the user's query
        Set<String> userKeywords = preprocessor.extractKeywords(userInput);
        
        List<EvidenceDto> allEvidence = new ArrayList<>();
        int highestMatchScore = 0;
        String finalVerdict = "UNVERIFIED";
        String finalExplanation = "";

        // ==========================================
        // STEP 1: Check Local MySQL Database
        // ==========================================
        String primaryKeyword = userKeywords.stream().findFirst().orElse("");
        List<FactCheckRecord> dbMatches = dbRepository.findByClaimTextContainingIgnoreCase(primaryKeyword);
        
        for (FactCheckRecord record : dbMatches) {
            Set<String> dbKeywords = preprocessor.extractKeywords(record.getClaimText());
            double score = similarityEngine.calculateJaccardSimilarity(userKeywords, dbKeywords);
            
            if (score > highestMatchScore) {
                highestMatchScore = (int) score;
                finalVerdict = record.getVerdict(); 
            }
            
            if (score > 30.0) { 
                allEvidence.add(EvidenceDto.builder()
                        .sourceName("TruthLens Core DB")
                        .title(record.getClaimText())
                        .url("#") 
                        .stance(record.getVerdict().toUpperCase().contains("TRUE") ? "SUPPORTS" : "CONTRADICTS")
                        .build());
            }
        }

        // ==========================================
        // STEP 2: Scrape Live News (Jsoup)
        // ==========================================
        // We use the full sentence for better scraping results instead of just 3 keywords
        String searchQuery = userInput.trim().replaceAll("\\s+", "+");
        List<EvidenceDto> scrapedNews = scraperService.scrapeNews(searchQuery);
        allEvidence.addAll(scrapedNews);

        // ==========================================
        // STEP 3: The Brain (AI) & Fault-Tolerant Backup
        // ==========================================
        if (highestMatchScore > 60) {
            if (highestMatchScore >= 85) highestMatchScore = 100; // Snapping to 100%
            if (finalVerdict.contains("TRUE")) finalVerdict = "TRUE";
            if (finalVerdict.contains("FALSE")) finalVerdict = "FALSE";
            finalExplanation = "We found a strong match in our historical fact-check database.";
        } 
        else if (!scrapedNews.isEmpty()) {
            try {
                // Try Gemini AI first
                Map<String, Object> aiResult = aiAnalysisService.analyzeClaim(userInput, scrapedNews);
                
                finalVerdict = (String) aiResult.get("verdict");
                highestMatchScore = (Integer) aiResult.get("confidenceScore");
                finalExplanation = (String) aiResult.get("explanation");
                
                if (highestMatchScore >= 90) highestMatchScore = 100; // AI Snapping

            } catch (Exception e) {
                System.out.println("⚠️ Gemini API limit exceeded. Switching to Local Scoring Engine!");
                
                // 🔥 FALLBACK: Uses userKeywords to filter out irrelevant news (like the CA Exams issue)
                LocalScoringEngine.ScoringResult localResult = localScoringEngine.calculate(userInput, scrapedNews, userKeywords);
                
                finalVerdict = localResult.getVerdict();
                highestMatchScore = localResult.getScore();
                finalExplanation = "⚠️ AI API limit exceeded. Evaluated via Local Engine: We found " + localResult.getSupports() + 
                                   " supporting and " + localResult.getContradicts() + " contradicting relevant sources.";
            }
        } 
        else {
            finalVerdict = "UNVERIFIABLE";
            highestMatchScore = 15;
            finalExplanation = "TruthLens algorithms and web scrapers could not find sufficient evidence on the internet or local database.";
        }

        // ==========================================
        // STEP 4: Package everything using Lombok Builder
        // ==========================================
        ClaimDto claimDto = ClaimDto.builder()
                .claimText(userInput) 
                .verdict(finalVerdict)
                .evidence(allEvidence)
                .build();

        return VerificationResponse.builder()
                .verdict(finalVerdict)
                .confidenceScore(highestMatchScore)
                .explanation(finalExplanation)
                .claims(List.of(claimDto))
                .build();
    }
}
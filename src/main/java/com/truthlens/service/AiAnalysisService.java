//////package com.truthlens.service;
//////
//////import com.fasterxml.jackson.databind.JsonNode;
//////import com.fasterxml.jackson.databind.ObjectMapper;
//////import com.truthlens.dto.response.EvidenceDto;
//////
////////import com.truthlens.dto.EvidenceDto;
//////import org.springframework.stereotype.Service;
//////import org.springframework.web.client.RestTemplate;
//////import org.springframework.http.HttpEntity;
//////import org.springframework.http.HttpHeaders;
//////import org.springframework.http.MediaType;
//////
//////import java.util.List;
//////import java.util.Map;
//////
//////@Service
//////public class AiAnalysisService {
//////
//////    // ⚠️ IMPORTANT: Yahan apni API Key paste karo!
//////    private static final String GEMINI_API_KEY = "YOUR_GEMINI_API_KEY_HERE"; 
//////    
//////    // Using Gemini 1.5 Flash API endpoint
//////    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + GEMINI_API_KEY;
//////
//////    public Map<String, Object> analyzeClaim(String claim, List<EvidenceDto> evidences) {
//////        try {
//////            // 1. Jsoup evidence ko text mein convert kar rahe hain
//////            StringBuilder evidenceText = new StringBuilder();
//////            for (EvidenceDto ev : evidences) {
//////                evidenceText.append("- Headline: ").append(ev.getTitle())
//////                            .append(" (Source: ").append(ev.getSourceName()).append(")\n");
//////            }
//////
//////            // 2. Strict Prompt for the AI
//////            String prompt = "You are an expert Fact-Checking AI for a system called TruthLens. " +
//////                    "Analyze the user's claim based ONLY on the provided scraped news evidence.\n\n" +
//////                    "User Claim: \"" + claim + "\"\n" +
//////                    "Scraped Evidence:\n" + evidenceText.toString() + "\n\n" +
//////                    "Respond EXACTLY in this JSON format without any markdown, backticks or extra words:\n" +
//////                    "{\"verdict\": \"LIKELY TRUE\" or \"LIKELY FALSE\" or \"MIXED\", \"confidenceScore\": <number between 60 to 99>, \"explanation\": \"<1-2 short powerful sentences explaining the reality based on the evidence>\"}";
//////
//////            // 3. API Request Setup
//////            RestTemplate restTemplate = new RestTemplate();
//////            HttpHeaders headers = new HttpHeaders();
//////            headers.setContentType(MediaType.APPLICATION_JSON);
//////
//////            // Clean strings so JSON doesn't break
//////            String cleanPrompt = prompt.replace("\"", "\\\"").replace("\n", "\\n");
//////            String requestBody = "{ \"contents\": [ { \"parts\": [ { \"text\": \"" + cleanPrompt + "\" } ] } ] }";
//////            
//////            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
//////
//////            // 4. Hit the Gemini API
//////            String response = restTemplate.postForObject(GEMINI_URL, request, String.class);
//////
//////            // 5. Read the AI JSON Response
//////            ObjectMapper mapper = new ObjectMapper();
//////            JsonNode root = mapper.readTree(response);
//////            String aiResponseText = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
//////
//////            // Clean markdown blocks if Gemini accidentally sends them
//////            aiResponseText = aiResponseText.replace("```json", "").replace("```", "").trim();
//////
//////            JsonNode resultNode = mapper.readTree(aiResponseText);
//////
//////            return Map.of(
//////                    "verdict", resultNode.get("verdict").asText(),
//////                    "confidenceScore", resultNode.get("confidenceScore").asInt(),
//////                    "explanation", resultNode.get("explanation").asText()
//////            );
//////
//////        } catch (Exception e) {
//////            System.err.println("❌ AI Engine Error: " + e.getMessage());
//////            // Fail-safe default agar API fail ho jaye
//////            return Map.of(
//////                    "verdict", "NEUTRAL / RECENT NEWS",
//////                    "confidenceScore", 50,
//////                    "explanation", "We scraped live news but the AI Brain (Gemini) failed to process the verification. Error: " + e.getMessage()
//////            );
//////        }
//////    }
//////}
////
////package com.truthlens.service;
////
////import com.fasterxml.jackson.databind.JsonNode;
////import com.fasterxml.jackson.databind.ObjectMapper;
////
////// YEH WALA IMPORT TUMHARE PROJECT KE HISAAB SE HAI
////import com.truthlens.dto.response.EvidenceDto; 
////
////import org.springframework.stereotype.Service;
////import org.springframework.web.client.RestTemplate;
////import org.springframework.http.HttpEntity;
////import org.springframework.http.HttpHeaders;
////import org.springframework.http.MediaType;
////
////import java.util.List;
////import java.util.Map;
////
////@Service
////public class AiAnalysisService {
////
////    // ⚠️ IMPORTANT: Yahan apni API Key paste karo!
////    private static final String GEMINI_API_KEY = "AIzaSyC80UkA8KJ1RlxVxc2j6_eDRH8Pq6cz64o"; 
//// 
//// // Updated to use the latest stable Gemini 2.5 Flash model
////    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + GEMINI_API_KEY;
////    
//////    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent" + GEMINI_API_KEY;
////
////    public Map<String, Object> analyzeClaim(String claim, List<EvidenceDto> evidences) {
////        try {
////            StringBuilder evidenceText = new StringBuilder();
////            for (EvidenceDto ev : evidences) {
////                evidenceText.append("- Headline: ").append(ev.getTitle())
////                            .append(" (Source: ").append(ev.getSourceName()).append(")\n");
////            }
////
////            String prompt = "You are an expert Fact-Checking AI for a system called TruthLens. " +
////                    "Analyze the user's claim based ONLY on the provided scraped news evidence.\n\n" +
////                    "User Claim: \"" + claim + "\"\n" +
////                    "Scraped Evidence:\n" + evidenceText.toString() + "\n\n" +
////                    "Respond EXACTLY in this JSON format without any markdown, backticks or extra words:\n" +
////                    "{\"verdict\": \"LIKELY TRUE\" or \"LIKELY FALSE\" or \"MIXED\", \"confidenceScore\": <number between 60 to 99>, \"explanation\": \"<1-2 short powerful sentences explaining the reality based on the evidence>\"}";
////
////            RestTemplate restTemplate = new RestTemplate();
////            HttpHeaders headers = new HttpHeaders();
////            headers.setContentType(MediaType.APPLICATION_JSON);
////
////            String cleanPrompt = prompt.replace("\"", "\\\"").replace("\n", "\\n");
////            String requestBody = "{ \"contents\": [ { \"parts\": [ { \"text\": \"" + cleanPrompt + "\" } ] } ] }";
////            
////            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
////
////            String response = restTemplate.postForObject(GEMINI_URL, request, String.class);
////
////            ObjectMapper mapper = new ObjectMapper();
////            JsonNode root = mapper.readTree(response);
////            String aiResponseText = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
////
////            aiResponseText = aiResponseText.replace("```json", "").replace("```", "").trim();
////
////            JsonNode resultNode = mapper.readTree(aiResponseText);
////
////            return Map.of(
////                    "verdict", resultNode.get("verdict").asText(),
////                    "confidenceScore", resultNode.get("confidenceScore").asInt(),
////                    "explanation", resultNode.get("explanation").asText()
////            );
////
////        } catch (Exception e) {
////            System.err.println("❌ AI Engine Error: " + e.getMessage());
////            return Map.of(
////                    "verdict", "NEUTRAL / RECENT NEWS",
////                    "confidenceScore", 50,
////                    "explanation", "We scraped live news but the AI Brain (Gemini) failed to process the verification. Error: " + e.getMessage()
////            );
////        }
////    }
////}
//
//package com.truthlens.service;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.truthlens.dto.response.EvidenceDto; 
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class AiAnalysisService {
//
//    // ⚠️ IMPORTANT: Yahan bas apni API key daalna bina kisi extra space ke
//    private static final String GEMINI_API_KEY = "AIzaSyC80UkA8KJ1RlxVxc2j6_eDRH8Pq6cz64o"; 
//    
//    // Stable URL with ?key= correctly appended
//    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + GEMINI_API_KEY;
//
//    public Map<String, Object> analyzeClaim(String claim, List<EvidenceDto> evidences) {
//        try {
//            StringBuilder evidenceText = new StringBuilder();
//            for (EvidenceDto ev : evidences) {
//                evidenceText.append("- Headline: ").append(ev.getTitle())
//                            .append(" (Source: ").append(ev.getSourceName()).append(")\n");
//            }
//
//            // NAYA PROMPT: "TRUE"/"FALSE" aur 100 score allowed
//            String prompt = "You are an expert Fact-Checking AI for a system called TruthLens. " +
//                    "Analyze the user's claim based ONLY on the provided scraped news evidence.\n\n" +
//                    "User Claim: \"" + claim + "\"\n" +
//                    "Scraped Evidence:\n" + evidenceText.toString() + "\n\n" +
//                    "Respond EXACTLY in this JSON format without any markdown, backticks or extra words:\n" +
//                    "{\"verdict\": \"TRUE\" or \"FALSE\" or \"MIXED\", \"confidenceScore\": <number between 60 to 100. Give EXACTLY 100 if the evidence is completely clear and definitive>, \"explanation\": \"<1-2 short powerful sentences explaining the reality based on the evidence>\"}";
//
//            RestTemplate restTemplate = new RestTemplate();
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//
//            String cleanPrompt = prompt.replace("\"", "\\\"").replace("\n", "\\n");
//            String requestBody = "{ \"contents\": [ { \"parts\": [ { \"text\": \"" + cleanPrompt + "\" } ] } ] }";
//            
//            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
//
//            String response = restTemplate.postForObject(GEMINI_URL, request, String.class);
//
//            ObjectMapper mapper = new ObjectMapper();
//            JsonNode root = mapper.readTree(response);
//            String aiResponseText = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
//
//            aiResponseText = aiResponseText.replace("```json", "").replace("```", "").trim();
//
//            JsonNode resultNode = mapper.readTree(aiResponseText);
//
//            return Map.of(
//                    "verdict", resultNode.get("verdict").asText(),
//                    "confidenceScore", resultNode.get("confidenceScore").asInt(),
//                    "explanation", resultNode.get("explanation").asText()
//            );
//
//        } catch (Exception e) {
//            System.err.println("❌ AI Engine Error: " + e.getMessage());
//            return Map.of(
//                    "verdict", "NEUTRAL / RECENT NEWS",
//                    "confidenceScore", 50,
//                    "explanation", "We scraped live news but the AI Brain (Gemini) failed to process the verification. Error: " + e.getMessage()
//            );
//        }
//    }
//}

package com.truthlens.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.truthlens.dto.response.EvidenceDto; 
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

@Service
public class AiAnalysisService {

    // ⚠️ IMPORTANT: Yahan apni API key daalna!
    private static final String GEMINI_API_KEY = "GEMINI_API_KEY"; 
    
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + GEMINI_API_KEY;

    public Map<String, Object> analyzeClaim(String claim, List<EvidenceDto> evidences) throws Exception {
        try {
            StringBuilder evidenceText = new StringBuilder();
            for (EvidenceDto ev : evidences) {
                evidenceText.append("- Headline: ").append(ev.getTitle())
                            .append(" (Source: ").append(ev.getSourceName()).append(")\n");
            }

            String prompt = "You are an expert Fact-Checking AI for a system called TruthLens. " +
                    "Analyze the user's claim based ONLY on the provided scraped news evidence.\n\n" +
                    "User Claim: \"" + claim + "\"\n" +
                    "Scraped Evidence:\n" + evidenceText.toString() + "\n\n" +
                    "Respond EXACTLY in this JSON format without any markdown, backticks or extra words:\n" +
                    "{\"verdict\": \"TRUE\" or \"FALSE\" or \"MIXED\", \"confidenceScore\": <number between 60 to 100. Give EXACTLY 100 if the evidence is completely clear and definitive>, \"explanation\": \"<1-2 short powerful sentences explaining the reality based on the evidence>\"}";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String cleanPrompt = prompt.replace("\"", "\\\"").replace("\n", "\\n");
            String requestBody = "{ \"contents\": [ { \"parts\": [ { \"text\": \"" + cleanPrompt + "\" } ] } ] }";
            
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            String response = restTemplate.postForObject(GEMINI_URL, request, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            String aiResponseText = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

            aiResponseText = aiResponseText.replace("```json", "").replace("```", "").trim();
            JsonNode resultNode = mapper.readTree(aiResponseText);

            return Map.of(
                    "verdict", resultNode.get("verdict").asText(),
                    "confidenceScore", resultNode.get("confidenceScore").asInt(),
                    "explanation", resultNode.get("explanation").asText()
            );

        } catch (Exception e) {
            // Yeh error sirf Developer ko Console mein dikhega
            System.err.println("❌ AI Engine Error: " + e.getMessage());
            // Yeh throw hone se VerificationService apna Local Engine on kar dega
            throw new Exception("API_FAILED"); 
        }
    }
}
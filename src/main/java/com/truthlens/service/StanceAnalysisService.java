//package com.truthlens.service;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.truthlens.client.GeminiClient;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class StanceAnalysisService {
//
//    private final GeminiClient geminiClient;
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    public String analyzeStance(String claim, String title, String summary) {
//
//        String prompt = """
//                You are an expert fact-checking assistant.
//
//                Determine the stance of the article toward the claim.
//
//                Claim:
//                "%s"
//
//                Article Title:
//                "%s"
//
//                Article Summary:
//                "%s"
//
//                Rules:
//                - Return ONLY valid JSON.
//                - No explanation.
//                - No markdown.
//                - No backticks.
//                - Only JSON.
//                - Output format:
//
//                {
//                  "stance": "SUPPORT | CONTRADICT | PARTIAL | UNRELATED"
//                }
//                """.formatted(claim, title, summary);
//
//        String response = geminiClient.generate(prompt);
//
//        return parseStance(response);
//    }
//
//    private String parseStance(String rawResponse) {
//
//        try {
//
//            String cleaned = cleanJson(rawResponse);
//
//            JsonNode root = objectMapper.readTree(cleaned);
//
//            if (root.has("stance")) {
//                return root.get("stance").asText();
//            }
//
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to parse stance from Gemini: " + rawResponse);
//        }
//
//        return "UNRELATED";
//    }
//
//    private String cleanJson(String text) {
//
//        text = text.trim();
//
//        if (text.startsWith("```")) {
//            text = text.replace("```json", "")
//                       .replace("```", "")
//                       .trim();
//        }
//
//        return text;
//    }
//}

package com.truthlens.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.truthlens.client.GeminiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StanceAnalysisService {

    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String analyzeStance(String claim, String title, String summary) {

        String prompt = """
                You are a professional fact verification AI.

                Determine whether the ARTICLE supports or contradicts the CLAIM.

                Rules:
                - If the article clearly confirms the claim is true → SUPPORT
                - If the article clearly states the claim is false → CONTRADICT
                - If it confirms only part → PARTIAL
                - If the article discusses the topic but does not clearly confirm or deny → UNRELATED
                - If unsure, return UNRELATED

                Return ONLY valid JSON:

                {
                  "stance": "SUPPORT | CONTRADICT | PARTIAL | UNRELATED"
                }

                CLAIM:
                "%s"

                ARTICLE TITLE:
                "%s"

                ARTICLE SUMMARY:
                "%s"
                """.formatted(claim, title, summary);

        String response = geminiClient.generate(prompt);

        return parseStance(response);
    }

    private String parseStance(String rawResponse) {

        try {
            String cleaned = rawResponse.trim();

            if (cleaned.startsWith("```")) {
                cleaned = cleaned.replace("```json", "")
                        .replace("```", "")
                        .trim();
            }

            JsonNode root = objectMapper.readTree(cleaned);

            if (root.has("stance")) {
                return root.get("stance").asText();
            }

        } catch (Exception e) {
            return "UNRELATED";
        }

        return "UNRELATED";
    }
}

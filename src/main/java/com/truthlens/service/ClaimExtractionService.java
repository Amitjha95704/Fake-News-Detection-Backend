package com.truthlens.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.truthlens.client.GeminiClient;
import com.truthlens.model.internal.Claim;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClaimExtractionService {

    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Claim> extractClaims(String content) {

        String prompt = """
                You are a fact verification assistant.

                Extract clear, factual, verifiable claims from the text below.

                Rules:
                - Return ONLY a valid JSON array of strings.
                - No explanation.
                - No markdown formatting.
                - No backticks.
                - No extra text.
                - Only JSON.

                Example Output:
                ["Claim 1", "Claim 2"]

                Text:
                """ + content;

        String response = geminiClient.generate(prompt);

        return parseClaims(response);
    }

    private List<Claim> parseClaims(String rawResponse) {

        List<Claim> claims = new ArrayList<>();

        try {

            String cleaned = cleanJson(rawResponse);

            JsonNode jsonArray = objectMapper.readTree(cleaned);

            if (jsonArray.isArray()) {
                for (JsonNode node : jsonArray) {
                    claims.add(new Claim(node.asText()));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse claims from Gemini response: " + rawResponse);
        }

        return claims;
    }

    /**
     * Removes unwanted markdown or backticks from Gemini response.
     */
    private String cleanJson(String text) {

        text = text.trim();

        if (text.startsWith("```")) {
            text = text.replace("```json", "")
                       .replace("```", "")
                       .trim();
        }

        return text;
    }
}

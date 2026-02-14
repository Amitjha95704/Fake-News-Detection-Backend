package com.truthlens.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class GeminiClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${api.gemini.key}")
    private String apiKey;

    @Value("${api.gemini.url}")
    private String apiUrl;

    /**
     * Sends prompt to Gemini and returns generated text.
     */
    public String generate(String prompt) {

        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of(
                                "parts", new Object[]{
                                        Map.of("text", prompt)
                                }
                        )
                }
        );

        try {
            String rawResponse = webClient.post()
                    .uri(apiUrl + "?key=" + apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return extractText(rawResponse);

        } catch (WebClientResponseException e) {
            throw new RuntimeException("Gemini API error: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("Error calling Gemini API: " + e.getMessage());
        }
    }

    /**
     * Extracts text content from Gemini JSON response.
     */
    private String extractText(String rawJson) {

        try {
            JsonNode root = objectMapper.readTree(rawJson);

            return root
                    .get("candidates")
                    .get(0)
                    .get("content")
                    .get("parts")
                    .get(0)
                    .get("text")
                    .asText();

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Gemini response");
        }
    }
}

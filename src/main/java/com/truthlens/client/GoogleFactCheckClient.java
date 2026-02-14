//package com.truthlens.client;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.truthlens.model.internal.FactCheckResult;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.reactive.function.client.WebClientResponseException;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class GoogleFactCheckClient {
//
//    private final WebClient webClient;
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @Value("${api.factcheck.key}")
//    private String apiKey;
//
//    @Value("${api.factcheck.url}")
//    private String apiUrl;
//
//    /**
//     * Search fact-checked claims related to query.
//     */
//    public List<FactCheckResult> search(String query) {
//
//        List<FactCheckResult> results = new ArrayList<>();
//
//        try {
//
//            String response = webClient.get()
//                    .uri(uriBuilder -> uriBuilder
//                            .path(apiUrl.replace("https://factchecktools.googleapis.com", ""))
//                            .queryParam("query", query)
//                            .queryParam("key", apiKey)
//                            .build())
//                    .retrieve()
//                    .bodyToMono(String.class)
//                    .block();
//
//            JsonNode root = objectMapper.readTree(response);
//            JsonNode claims = root.get("claims");
//
//            if (claims != null && claims.isArray()) {
//
//                for (JsonNode claimNode : claims) {
//
//                    FactCheckResult result = new FactCheckResult();
//
//                    result.setClaim(claimNode.get("text") != null
//                            ? claimNode.get("text").asText()
//                            : "");
//
//                    JsonNode claimReviews = claimNode.get("claimReview");
//
//                    if (claimReviews != null && claimReviews.isArray() && claimReviews.size() > 0) {
//
//                        JsonNode review = claimReviews.get(0);
//
//                        result.setVerdict(review.get("textualRating") != null
//                                ? review.get("textualRating").asText()
//                                : "Unknown");
//
//                        result.setUrl(review.get("url") != null
//                                ? review.get("url").asText()
//                                : "");
//                    }
//
//                    results.add(result);
//                }
//            }
//
//        } catch (WebClientResponseException e) {
//            throw new RuntimeException("FactCheck API error: " + e.getResponseBodyAsString());
//        } catch (Exception e) {
//            throw new RuntimeException("Error calling FactCheck API: " + e.getMessage());
//        }
//
//        return results;
//    }
//}

package com.truthlens.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.truthlens.model.internal.FactCheckResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GoogleFactCheckClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${api.factcheck.key}")
    private String apiKey;

    @Value("${api.factcheck.url}")
    private String apiUrl;

    public List<FactCheckResult> search(String query) {

        List<FactCheckResult> results = new ArrayList<>();

        try {

            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("factchecktools.googleapis.com")
                            .path("/v1alpha1/claims:search")
                            .queryParam("query", query)
                            .queryParam("key", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(response);
            JsonNode claims = root.get("claims");

            if (claims != null && claims.isArray()) {
                for (JsonNode claimNode : claims) {

                    FactCheckResult result = new FactCheckResult();

                    result.setClaim(claimNode.get("text") != null
                            ? claimNode.get("text").asText()
                            : "");

                    JsonNode reviews = claimNode.get("claimReview");

                    if (reviews != null && reviews.isArray() && reviews.size() > 0) {
                        JsonNode review = reviews.get(0);

                        result.setVerdict(review.get("textualRating") != null
                                ? review.get("textualRating").asText()
                                : "Unknown");

                        result.setUrl(review.get("url") != null
                                ? review.get("url").asText()
                                : "");
                    }

                    results.add(result);
                }
            }

        } catch (WebClientResponseException e) {
            throw new RuntimeException("FactCheck API error: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("Error calling FactCheck API: " + e.getMessage());
        }

        return results;
    }
}

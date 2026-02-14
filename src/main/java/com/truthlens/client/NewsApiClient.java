////package com.truthlens.client;
////
////import com.fasterxml.jackson.databind.JsonNode;
////import com.fasterxml.jackson.databind.ObjectMapper;
////import com.truthlens.model.internal.ArticleResult;
////import lombok.RequiredArgsConstructor;
////import org.springframework.beans.factory.annotation.Value;
////import org.springframework.stereotype.Component;
////import org.springframework.web.reactive.function.client.WebClient;
////import org.springframework.web.reactive.function.client.WebClientResponseException;
////
////import java.util.ArrayList;
////import java.util.List;
////
////@Component
////@RequiredArgsConstructor
////public class NewsApiClient {
////
////    private final WebClient webClient;
////    private final ObjectMapper objectMapper = new ObjectMapper();
////
////    @Value("${api.news.key}")
////    private String apiKey;
////
////    @Value("${api.news.url}")
////    private String apiUrl;
////
////    /**
////     * Search news articles related to query.
////     */
////    public List<ArticleResult> search(String query) {
////
////        List<ArticleResult> results = new ArrayList<>();
////
////        try {
////
////            String response = webClient.get()
////                    .uri(uriBuilder -> uriBuilder
////                            .path(apiUrl.replace("https://newsapi.org", ""))
////                            .queryParam("q", query)
////                            .queryParam("language", "en")
////                            .queryParam("sortBy", "relevancy")
////                            .queryParam("pageSize", 5)
////                            .queryParam("apiKey", apiKey)
////                            .build())
////                    .retrieve()
////                    .bodyToMono(String.class)
////                    .block();
////
////            JsonNode root = objectMapper.readTree(response);
////            JsonNode articles = root.get("articles");
////
////            if (articles != null && articles.isArray()) {
////
////                for (JsonNode articleNode : articles) {
////
////                    ArticleResult article = new ArticleResult();
////
////                    article.setSource(
////                            articleNode.get("source") != null &&
////                            articleNode.get("source").get("name") != null
////                                    ? articleNode.get("source").get("name").asText()
////                                    : "Unknown Source"
////                    );
////
////                    article.setTitle(
////                            articleNode.get("title") != null
////                                    ? articleNode.get("title").asText()
////                                    : ""
////                    );
////
////                    article.setDescription(
////                            articleNode.get("description") != null
////                                    ? articleNode.get("description").asText()
////                                    : ""
////                    );
////
////                    article.setUrl(
////                            articleNode.get("url") != null
////                                    ? articleNode.get("url").asText()
////                                    : ""
////                    );
////
////                    results.add(article);
////                }
////            }
////
////        } catch (WebClientResponseException e) {
////            throw new RuntimeException("News API error: " + e.getResponseBodyAsString());
////        } catch (Exception e) {
////            throw new RuntimeException("Error calling News API: " + e.getMessage());
////        }
////
////        return results;
////    }
////}
//
//package com.truthlens.client;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.truthlens.model.internal.ArticleResult;
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
//public class NewsApiClient {
//
//    private final WebClient webClient;
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @Value("${api.news.key}")
//    private String apiKey;
//
//    public List<ArticleResult> search(String query) {
//
//        List<ArticleResult> results = new ArrayList<>();
//
//        try {
//
//            String response = webClient.get()
//                    .uri(uriBuilder -> uriBuilder
//                            .scheme("https")
//                            .host("gnews.io")
//                            .path("/api/v4/search")
//                            .queryParam("q", query)
//                            .queryParam("lang", "en")
//                            .queryParam("max", 5)
//                            .queryParam("apikey", apiKey)
//                            .build())
//                    .retrieve()
//                    .bodyToMono(String.class)
//                    .block();
//
//            JsonNode root = objectMapper.readTree(response);
//            JsonNode articles = root.get("articles");
//
//            if (articles != null && articles.isArray()) {
//
//                for (JsonNode articleNode : articles) {
//
//                    ArticleResult article = new ArticleResult();
//
//                    article.setSource(
//                            articleNode.get("source") != null &&
//                            articleNode.get("source").get("name") != null
//                                    ? articleNode.get("source").get("name").asText()
//                                    : "Unknown Source"
//                    );
//
//                    article.setTitle(
//                            articleNode.get("title") != null
//                                    ? articleNode.get("title").asText()
//                                    : ""
//                    );
//
//                    article.setDescription(
//                            articleNode.get("description") != null
//                                    ? articleNode.get("description").asText()
//                                    : ""
//                    );
//
//                    article.setUrl(
//                            articleNode.get("url") != null
//                                    ? articleNode.get("url").asText()
//                                    : ""
//                    );
//
//                    results.add(article);
//                }
//            }
//
//        } catch (WebClientResponseException e) {
//            throw new RuntimeException("GNews API error: " + e.getResponseBodyAsString());
//        } catch (Exception e) {
//            throw new RuntimeException("Error calling GNews API: " + e.getMessage());
//        }
//
//        return results;
//    }
//}

package com.truthlens.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.truthlens.model.internal.ArticleResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NewsApiClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${api.news.key}")
    private String apiKey;

    public List<ArticleResult> search(String query) {

        List<ArticleResult> results = new ArrayList<>();

        try {

            // 🔥 Clean the query (remove special characters)
            String cleanedQuery = query
                    .replaceAll("[^a-zA-Z0-9\\s]", "")
                    .trim();

            // Limit length (GNews dislikes long queries)
            if (cleanedQuery.length() > 100) {
                cleanedQuery = cleanedQuery.substring(0, 100);
            }

            // Proper URL encoding
            String encodedQuery = URLEncoder.encode(cleanedQuery, StandardCharsets.UTF_8);

            String url = "https://gnews.io/api/v4/search?q=" +
                    encodedQuery +
                    "&lang=en&max=5&apikey=" +
                    apiKey;

            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(response);
            JsonNode articles = root.get("articles");

            if (articles != null && articles.isArray()) {

                for (JsonNode articleNode : articles) {

                    ArticleResult article = new ArticleResult();

                    article.setSource(
                            articleNode.get("source") != null &&
                            articleNode.get("source").get("name") != null
                                    ? articleNode.get("source").get("name").asText()
                                    : "Unknown Source"
                    );

                    article.setTitle(
                            articleNode.get("title") != null
                                    ? articleNode.get("title").asText()
                                    : ""
                    );

                    article.setDescription(
                            articleNode.get("description") != null
                                    ? articleNode.get("description").asText()
                                    : ""
                    );

                    article.setUrl(
                            articleNode.get("url") != null
                                    ? articleNode.get("url").asText()
                                    : ""
                    );

                    results.add(article);
                }
            }

        } catch (WebClientResponseException e) {

            String errorBody = e.getResponseBodyAsString();

            if (errorBody != null && errorBody.contains("too many requests")) {
                System.out.println("⚠ GNews rate limit reached. Skipping news fetch.");
                return new ArrayList<>();
            }

            throw new RuntimeException("GNews API error: " + errorBody);
        }

            catch (Exception e) {
            throw new RuntimeException("Error calling GNews API: " + e.getMessage());
        }

        return results;
    }
}

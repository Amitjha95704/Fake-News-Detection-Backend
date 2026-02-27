//package com.truthlens.service;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import java.net.URLDecoder;
//import java.nio.charset.StandardCharsets;
//
//@Service
//public class TranslationService {
//
//    private final RestTemplate restTemplate = new RestTemplate();
//    private final ObjectMapper mapper = new ObjectMapper();
//
//    public String translate(String text, String sourceLang, String targetLang) {
//        try {
//            String url = UriComponentsBuilder.fromHttpUrl("https://translate.googleapis.com/translate_a/single")
//                    .queryParam("client", "gtx")
//                    .queryParam("sl", sourceLang)
//                    .queryParam("tl", targetLang)
//                    .queryParam("dt", "t")
//                    .queryParam("q", text)
//                    .toUriString();
//
//            String response = restTemplate.getForObject(url, String.class);
//            JsonNode root = mapper.readTree(response);
//            
//            // Raw text nikalna
//            String rawTranslatedText = root.get(0).get(0).get(0).asText();
//            
//            // 🔥 THE FIX: %20 aur ajeeb characters ko clean karna
//            return URLDecoder.decode(rawTranslatedText, StandardCharsets.UTF_8);
//            
//        } catch (Exception e) {
//            System.err.println("⚠️ Translation Error: " + e.getMessage());
//            return text; // Fallback
//        }
//    }
//
//    public String detectLanguage(String text) {
//        try {
//            String url = UriComponentsBuilder.fromHttpUrl("https://translate.googleapis.com/translate_a/single")
//                    .queryParam("client", "gtx")
//                    .queryParam("sl", "auto")
//                    .queryParam("tl", "en")
//                    .queryParam("dt", "t")
//                    .queryParam("q", text)
//                    .toUriString();
//
//            String response = restTemplate.getForObject(url, String.class);
//            JsonNode root = mapper.readTree(response);
//            
//            return root.get(2).asText(); 
//            
//        } catch (Exception e) {
//            return "en"; 
//        }
//    }
//}
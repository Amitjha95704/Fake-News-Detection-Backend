//package com.truthlens.service;
//
//import com.truthlens.client.GeminiClient;
//import com.truthlens.model.internal.Claim;
//import com.truthlens.model.response.Evidence;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class ExplanationService {
//
//    private final GeminiClient geminiClient;
//
//    public String generateExplanation(List<Claim> claims,
//                                      List<Evidence> evidenceList,
//                                      int supportCount,
//                                      int contradictCount) {
//
//        String claimText = claims.stream()
//                .map(Claim::getText)
//                .collect(Collectors.joining("; "));
//
//        String evidenceSummary = evidenceList.stream()
//                .limit(5)
//                .map(e -> e.getSource() + ": " + e.getTitle())
//                .collect(Collectors.joining("\n"));
//
//        String prompt = """
//                You are an AI fact verification assistant.
//
//                Claims:
//                %s
//
//                Supporting Sources: %d
//                Contradicting Sources: %d
//
//                Evidence Titles:
//                %s
//
//                Write a short explanation (4-6 sentences) summarizing:
//                - Whether the claim is likely true or false
//                - Why the verdict was reached
//                - Mention presence of contradicting or supporting evidence
//                - Use simple language
//
//                Return only plain text. No markdown.
//                """.formatted(
//                claimText,
//                supportCount,
//                contradictCount,
//                evidenceSummary
//        );
//
//        return geminiClient.generate(prompt);
//    }
//}

package com.truthlens.service;

import com.truthlens.client.GeminiClient;
import com.truthlens.model.internal.Claim;
import com.truthlens.model.response.Evidence;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExplanationService {

    private final GeminiClient geminiClient;

    public String generateExplanation(List<Claim> claims,
                                      List<Evidence> evidenceList,
                                      int supportCount,
                                      int contradictCount) {

        String claimText = claims.stream()
                .map(Claim::getText)
                .collect(Collectors.joining("; "));

        String prompt = """
                You are an AI fact verification assistant.

                Claims:
                %s

                Supporting Sources: %d
                Contradicting Sources: %d

                Write a short explanation (4-6 sentences) explaining:
                - Whether the claim is likely true or false
                - Why this conclusion was reached
                - Mention if sources confirm or contradict
                - Use simple and neutral language

                Return plain text only.
                """.formatted(claimText, supportCount, contradictCount);

        return geminiClient.generate(prompt);
    }
}


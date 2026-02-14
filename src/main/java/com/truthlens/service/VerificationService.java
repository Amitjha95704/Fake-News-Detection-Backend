package com.truthlens.service;

import com.truthlens.client.GoogleFactCheckClient;
import com.truthlens.client.NewsApiClient;
import com.truthlens.model.internal.ArticleResult;
import com.truthlens.model.internal.Claim;
import com.truthlens.model.internal.FactCheckResult;
import com.truthlens.model.request.VerificationRequest;
import com.truthlens.model.response.Evidence;
import com.truthlens.model.response.VerificationResponse;
import com.truthlens.model.response.VerdictType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final ClaimExtractionService claimExtractionService;
    private final GoogleFactCheckClient factCheckClient;
    private final NewsApiClient newsApiClient;
    private final StanceAnalysisService stanceAnalysisService;
    private final ExplanationService explanationService;
    private final ScoringService scoringService;

    public VerificationResponse verify(VerificationRequest request) {

        // 1️⃣ Extract Claims
        List<Claim> claims = claimExtractionService.extractClaims(request.getContent());

        List<Evidence> evidenceList = new ArrayList<>();

        int support = 0;
        int contradict = 0;
        int partial = 0;
        int unrelated = 0;

        // 2️⃣ Process each claim
        for (Claim claim : claims) {

            String claimText = claim.getText();

            // 🔍 Google Fact Check
            List<FactCheckResult> factChecks = factCheckClient.search(claimText);

            for (FactCheckResult fc : factChecks) {

                String stance = "UNRELATED";

                if (fc.getVerdict() != null) {
                    String verdictLower = fc.getVerdict().toLowerCase();

                    if (verdictLower.contains("false")) {
                        stance = "CONTRADICT";
                        contradict++;
                    } else if (verdictLower.contains("true")) {
                        stance = "SUPPORT";
                        support++;
                    } else {
                        stance = "PARTIAL";
                        partial++;
                    }
                }

                evidenceList.add(new Evidence(
                        "Google Fact Check",
                        fc.getClaim(),
                        fc.getVerdict(),
                        fc.getUrl(),
                        stance
                ));
            }

            // 📰 News API
            List<ArticleResult> articles = newsApiClient.search(claimText);

            for (ArticleResult article : articles) {

                String stance = stanceAnalysisService.analyzeStance(
                        claimText,
                        article.getTitle(),
                        article.getDescription()
                );

                switch (stance) {
                    case "SUPPORT" -> support++;
                    case "CONTRADICT" -> contradict++;
                    case "PARTIAL" -> partial++;
                    default -> unrelated++;
                }

                evidenceList.add(new Evidence(
                        article.getSource(),
                        article.getTitle(),
                        article.getDescription(),
                        article.getUrl(),
                        stance
                ));
            }
        }

        // 3️⃣ Calculate Confidence
        int confidence = scoringService.calculateConfidence(
                support, contradict, partial, unrelated
        );

        // 4️⃣ Determine Verdict
        VerdictType verdict = scoringService.determineVerdict(
                support, contradict, partial, unrelated
        );

        // 5️⃣ Generate Explanation
        String explanation = explanationService.generateExplanation(
                claims,
                evidenceList,
                support,
                contradict
        );

        // 6️⃣ Return Response
        return VerificationResponse.builder()
                .verdict(verdict)
                .confidence(confidence)
                .explanation(explanation)
                .evidence(evidenceList)
                .build();
    }
}

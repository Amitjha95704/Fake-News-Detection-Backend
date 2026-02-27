package com.truthlens.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationResponse {
    private String verdict;          // The overall system verdict
    private int confidenceScore;     // 0-100 calculated by our custom math algorithm
    private String explanation;      // A generated summary of why we gave this score
    private List<ClaimDto> claims;   // The detailed breakdown
}
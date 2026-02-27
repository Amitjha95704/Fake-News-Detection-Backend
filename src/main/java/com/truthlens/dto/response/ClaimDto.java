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
public class ClaimDto {
    private String claimText;           // The sentence we are verifying
    private String verdict;             // "LIKELY TRUE", "LIKELY FALSE", etc.
    private List<EvidenceDto> evidence; // The scraped articles and DB matches for this claim
}
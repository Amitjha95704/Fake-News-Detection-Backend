package com.truthlens.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvidenceDto {
    private String sourceName; // e.g., "TruthLens Local DB" or "Scraped Article"
    private String title;      // The headline or the matched text
    private String url;        // Link to the source (or "#" if it's from local DB)
    private String stance;     // "SUPPORTS", "CONTRADICTS", or "NEUTRAL"
}
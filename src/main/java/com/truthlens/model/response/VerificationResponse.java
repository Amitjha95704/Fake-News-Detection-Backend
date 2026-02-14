package com.truthlens.model.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class VerificationResponse {

    private VerdictType verdict;
    private int confidence;
    private String explanation;
    private List<Evidence> evidence;
}

package com.truthlens.model.internal;

import lombok.Data;

@Data
public class FactCheckResult {

    private String claim;
    private String verdict;
    private String url;
}

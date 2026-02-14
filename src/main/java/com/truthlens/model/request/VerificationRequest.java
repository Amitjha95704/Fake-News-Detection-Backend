package com.truthlens.model.request;

import lombok.Data;

@Data
public class VerificationRequest {

    private String type;      // text or url
    private String content;   // actual content
}

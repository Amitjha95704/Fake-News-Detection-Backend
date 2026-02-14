package com.truthlens.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Evidence {

    private String source;
    private String title;
    private String summary;
    private String url;
    private String stance;  // SUPPORT / CONTRADICT / PARTIAL / UNRELATED
}

package io.segmentme.analysis.api.dto;

import io.segmentme.analysis.dto.AnalysisData;
import lombok.Data;

@Data
public class SdkAnalysisRequest {
    private String contextId;

    private String contextKey;

    private AnalysisData analysisData;
}

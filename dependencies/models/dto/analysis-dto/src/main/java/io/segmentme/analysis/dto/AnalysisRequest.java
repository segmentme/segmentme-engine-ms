package io.segmentme.analysis.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AnalysisRequest {

    private String contextId;

    private String integrationPointKey;

    @NotNull
    private AnalysisData analysisData;

}

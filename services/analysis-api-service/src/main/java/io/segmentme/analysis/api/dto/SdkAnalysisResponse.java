package io.segmentme.analysis.api.dto;

import io.segmentme.analysis.dto.SegmentAnalysisResult;
import lombok.Data;

import java.util.List;

@Data
public class SdkAnalysisResponse {
    private String contextId;

    private List<SegmentAnalysisResult> analyzedSegments;
}

package io.segmentme.analysis.dto;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.analysis.dto.segment.SegmentDto;
import io.segmentme.models.shared.analysis.InlineType;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class CollectedAnalysysStatisticDto {
    private long analysisTime;


    private String integrationPointKey;

    private ContextDataHolder contextDataHolder;

    private List<SegmentDto> analyzedSegments;

    private List<SegmentAnalysisResult> segmentAnalysisResults;

    private Map<String, ConditionStatisticEntry> conditionResults = new HashMap<>();

    private String workspaceId;

    private JsonNode rawPayload;

    private String clientId;

    @Data
    public static class ConditionStatisticEntry {
        private boolean result;
        private String criteria;

        private String errors;
    }

    @Data
    public static class ContextDataHolder {
        private Map<String, Object> values;
        private Map<String, Object> extractedValues = new HashMap<>();
        private Map<String, InlineType> knownTypes;

    }
}

package io.segmentme.analysis.dto;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.analysis.dto.segment.SegmentDto;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DebugRequest {

    private String contextId;

    private String integrationPointKey;

    @NotNull
    private JsonNode payload;

    @NotNull
    private SegmentDto segment;
}


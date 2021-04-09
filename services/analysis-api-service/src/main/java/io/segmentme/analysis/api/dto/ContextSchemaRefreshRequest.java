package io.segmentme.analysis.api.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class ContextSchemaRefreshRequest {
    private String contextKey;

    private String integrationPointId;

    private JsonNode payload;
}

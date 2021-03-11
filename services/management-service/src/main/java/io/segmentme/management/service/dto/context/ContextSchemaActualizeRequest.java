package io.segmentme.management.service.dto.context;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class ContextSchemaActualizeRequest {
    private String integrationPointId;
    private String contextKey;
    private JsonNode payload;
}

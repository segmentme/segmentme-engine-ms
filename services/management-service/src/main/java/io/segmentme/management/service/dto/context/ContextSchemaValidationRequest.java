package io.segmentme.management.service.dto.context;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.management.service.domain.context.SchemaNode;
import lombok.Data;

@Data
public class ContextSchemaValidationRequest {
    private SchemaNode rootNode;

    private JsonNode rawPayload;
}

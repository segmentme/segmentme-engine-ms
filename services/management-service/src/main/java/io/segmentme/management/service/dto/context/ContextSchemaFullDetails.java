package io.segmentme.management.service.dto.context;

import io.segmentme.management.service.domain.context.SchemaNode;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class ContextSchemaFullDetails extends ContextSchemaBasicInfo {
    private String rawPayload;

    private SchemaNode rootNode;

    private Map<String, Object> nodeValues;
}

package io.segmentme.management.service.dto.context;

import io.segmentme.core.domain.context.SchemaNode;
import lombok.Data;

@Data
public class ContextSchemaCreateRequest {
    private SchemaNode rootNode;

    private String integrationPointKey;

    private String rawPayload;

    private String name;

    private String uniquenessIndicator;

}

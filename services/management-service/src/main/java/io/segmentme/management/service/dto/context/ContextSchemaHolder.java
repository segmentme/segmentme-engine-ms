package io.segmentme.management.service.dto.context;

import io.segmentme.core.domain.context.SchemaNode;
import io.segmentme.models.shared.analysis.InlineType;
import lombok.Data;

import java.util.Map;

@Data
public class ContextSchemaHolder {
    private String id;

    private String name;

    private SchemaNode rootNode;

    private Map<String, InlineType> inlinePath;

    private String integrationPointKey;

    private String rawPayload;

    private Map<String, Object> nodeValues;

    private String hash;
}

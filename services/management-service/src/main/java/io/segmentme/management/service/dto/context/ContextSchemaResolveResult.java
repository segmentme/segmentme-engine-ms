package io.segmentme.management.service.dto.context;

import io.segmentme.management.service.context.ContextSchemaValidationService;
import lombok.Data;

import java.util.List;

@Data
public class ContextSchemaResolveResult {
    private ContextSchemaFullDetails contextSchema;

    private List<ContextSchemaValidationService.SchemaValidationEntry> validationEntries;
}

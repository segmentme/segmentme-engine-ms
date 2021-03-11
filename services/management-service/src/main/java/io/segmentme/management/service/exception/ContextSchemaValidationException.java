package io.segmentme.management.service.exception;

import io.segmentme.management.service.context.ContextSchemaValidationService;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;


@EqualsAndHashCode(callSuper = true)
@Data
public class ContextSchemaValidationException extends ContextSchemaManagerException {
    private ArrayList<ContextSchemaValidationService.SchemaValidationEntry> schemaValidationResult;

}

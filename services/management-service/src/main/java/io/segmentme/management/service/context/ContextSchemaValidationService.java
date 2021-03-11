package io.segmentme.management.service.context;

import io.segmentme.core.domain.context.ContextSchema;
import lombok.Data;

import java.util.List;

public interface ContextSchemaValidationService {


    List<SchemaValidationEntry> validate(ContextSchema schema);

    @Data
    final class SchemaValidationEntry {
        private String code;
        private String path;
        private SeverityLevel severity;

        public SchemaValidationEntry setCode(String code) {
            this.code = code;
            this.severity = Errors.getSeverity(code);
            return this;
        }
    }

}

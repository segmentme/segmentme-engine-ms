package io.segmentme.management.service.service.context;

import io.segmentme.management.service.domain.context.ContextSchema;
import io.segmentme.models.shared.exception.Errors;
import io.segmentme.models.shared.exception.SeverityLevel;
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

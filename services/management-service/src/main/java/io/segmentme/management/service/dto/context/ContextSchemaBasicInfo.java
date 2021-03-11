package io.segmentme.management.service.dto.context;

import io.segmentme.models.shared.analysis.InlineType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class ContextSchemaBasicInfo extends ContextSchemaShortInfo {
    private Map<String, InlineType> inlinePath;
}

package io.segmentme.management.service.converter;

import io.segmentme.management.service.domain.context.ContextSchema;
import io.segmentme.management.service.dto.context.ContextSchemaHolder;

public class ContextSchemaConverter {

    private ContextSchemaConverter() {
    }

    public static ContextSchemaHolder toHolder(ContextSchema contextSchema) {
        return new ContextSchemaHolder().setId(contextSchema.getId())
            .setName(contextSchema.getName())
            .setUniquenessIndicator(contextSchema.getUniquenessIndicator())
            .setInlinePath(contextSchema.getInlinePath())
            .setRootNode(contextSchema.getRootNode())
            .setRawPayload(contextSchema.getRawPayload())
            .setNodeValues(contextSchema.getNodeValues())
            .setHash(contextSchema.getHash())
            .setIntegrationPointKey(contextSchema.getIntegrationPointKey());
    }

}

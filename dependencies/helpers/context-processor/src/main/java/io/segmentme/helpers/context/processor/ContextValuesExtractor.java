package io.segmentme.helpers.context.processor;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.core.domain.context.ContextSchema;
import io.segmentme.core.domain.workpsace.WorkspaceConfiguration;

public interface ContextValuesExtractor {

    ContextValueHolder extractValues(JsonNode rawContext, ContextSchema schema, WorkspaceConfiguration workspaceConfiguration);
}

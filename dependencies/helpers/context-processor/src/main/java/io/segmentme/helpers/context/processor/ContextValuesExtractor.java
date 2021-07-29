package io.segmentme.helpers.context.processor;

import com.fasterxml.jackson.databind.JsonNode;

public interface ContextValuesExtractor {
    ContextValuesExtractor INSTANCE = new ContextValuesExtractorImpl();

    <T extends SchemaDescriptor> ContextValueHolder<T> extractValues(JsonNode rawContext, T schema, ExtractorConfiguration configuration);
}

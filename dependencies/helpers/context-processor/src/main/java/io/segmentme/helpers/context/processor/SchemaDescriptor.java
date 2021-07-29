package io.segmentme.helpers.context.processor;

public interface SchemaDescriptor<T extends NodeDescriptor<T>> {
    NodeDescriptor<T> getRootNode();
}

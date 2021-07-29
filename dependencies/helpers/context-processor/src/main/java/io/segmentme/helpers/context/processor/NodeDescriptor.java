package io.segmentme.helpers.context.processor;

import io.segmentme.models.shared.analysis.SchemaNodeType;

import java.util.List;

public interface NodeDescriptor<T extends NodeDescriptor<T>> {
    List<T> getNodes();

    String getName();

    String getPath();

    SchemaNodeType getType();

    SchemaNodeType getSubType();
}

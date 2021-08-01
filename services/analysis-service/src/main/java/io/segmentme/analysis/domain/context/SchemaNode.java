package io.segmentme.analysis.domain.context;

import io.segmentme.core.domain.DbDomain;
import io.segmentme.helpers.context.processor.NodeDescriptor;
import io.segmentme.models.shared.analysis.SchemaNodeType;
import lombok.Data;

import java.util.List;

@Data
public class SchemaNode implements NodeDescriptor<SchemaNode>, DbDomain {
    private String name;

    private SchemaNodeType type;

    private SchemaNodeType subType;

    private String path;

    private List<SchemaNode> subNodes;

    @Override
    public List<SchemaNode> getNodes() {
        return subNodes;
    }
}

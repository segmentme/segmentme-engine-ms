package io.segmentme.access.service.domain.context;

import io.segmentme.models.shared.analysis.SchemaNodeType;
import lombok.Data;

import java.util.List;

@Data
public class SchemaNode {
    private String name;

    private SchemaNodeType type;

    private SchemaNodeType subType;

    private String path;

    private List<SchemaNode> subNodes;
}

package io.segmentme.management.service.domain.context;

import io.segmentme.core.domain.DbDomain;
import io.segmentme.models.shared.analysis.SchemaNodeType;
import lombok.Data;

import java.util.List;

@Data
public class SchemaNode implements DbDomain {
    private String name;

    private SchemaNodeType type;

    private SchemaNodeType subType;

    private String path;

    private List<SchemaNode> subNodes;
}

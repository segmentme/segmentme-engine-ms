package io.segmentme.models.shared.analysis;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InlineType {
    private SchemaNodeType rootType;
    private SchemaNodeType subType;

    public static InlineType of(SchemaNodeType rootType, SchemaNodeType subType) {
        return new InlineType(rootType, subType);
    }
}

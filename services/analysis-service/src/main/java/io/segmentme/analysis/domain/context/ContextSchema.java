package io.segmentme.analysis.domain.context;

import io.segmentme.core.domain.DbObject;
import io.segmentme.helpers.context.processor.SchemaDescriptor;
import io.segmentme.models.shared.analysis.InlineType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "contextSchema")
public class ContextSchema extends DbObject implements SchemaDescriptor<SchemaNode> {
    private SchemaNode rootNode;

    private Map<String, InlineType> inlinePath;

    @Indexed
    private String integrationPointKey;

    private String name;

    private String rawPayload;

    private Map<String, Object> nodeValues;

    private String hash;

    @Indexed
    private String uniquenessIndicator;


}

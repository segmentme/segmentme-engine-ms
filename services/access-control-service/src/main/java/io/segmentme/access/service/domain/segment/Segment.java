package io.segmentme.access.service.domain.segment;

import io.segmentme.core.domain.DbObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "segment")
@EqualsAndHashCode(callSuper = true)
public class Segment extends DbObject {
    @Indexed
    private String integrationPointKey;
}



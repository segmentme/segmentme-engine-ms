package io.segmentme.access.service.domain.context;

import io.segmentme.core.domain.DbObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "contextSchema")
public class ContextSchema extends DbObject {

    @Indexed
    private String integrationPointKey;


}

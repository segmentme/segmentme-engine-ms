package io.segmentme.access.service.domain.state;

import io.segmentme.core.domain.DbObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("state")
@EqualsAndHashCode(callSuper = true)
public class State extends DbObject {
    @Indexed
    private String integrationPointKey;

}

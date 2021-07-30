package io.segmentme.analysis.domain.state;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.analysis.domain.segment.Segment;
import io.segmentme.core.domain.DbObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("state")
@EqualsAndHashCode(callSuper = true)
public class State extends DbObject {

    private String name;

    private JsonNode value;

    private JsonNode defaultValue;

    private Segment segment;

    @Indexed
    private String integrationPointKey;

}

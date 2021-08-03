package io.segmentme.measurement.service.domain.statistic;

import io.segmentme.core.domain.DbObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "contextStatistic")
public class ContextStatistic extends DbObject {
    @Indexed(unique = true)
    private String contextId;

    private long totalParticipants;
    
}

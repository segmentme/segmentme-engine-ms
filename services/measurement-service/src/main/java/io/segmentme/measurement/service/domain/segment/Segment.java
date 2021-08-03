package io.segmentme.measurement.service.domain.segment;

import io.segmentme.core.domain.DbObject;
import io.segmentme.models.shared.analysis.AggregationType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "segment")
@EqualsAndHashCode(callSuper = true)
public class Segment extends DbObject {

    private AggregationType aggregation;

    @EqualsAndHashCode.Exclude
    private String name;

    private boolean active;

    @EqualsAndHashCode.Exclude
    private String description;

    @Indexed
    private String integrationPointKey;


    private boolean matchResult;

    @EqualsAndHashCode.Exclude
    private String hash;

    @Indexed
    private String contextId;

    private int openedForPercentage = 100;


}



package io.segmentme.access.service.domain.statistic;

import io.segmentme.core.domain.DbDomain;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AggregatedAnalysisCount implements DbDomain {
    private LocalDateTime dateTime;
    private int count;
    private String integrationPointKey;
    private long totalAnalysisTime;

}

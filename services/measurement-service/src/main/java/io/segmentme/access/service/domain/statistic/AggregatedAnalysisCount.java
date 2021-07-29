package io.segmentme.access.service.domain.statistic;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AggregatedAnalysisCount {
    private LocalDateTime dateTime;
    private int count;
    private String integrationPointKey;
    private long totalAnalysisTime;

}

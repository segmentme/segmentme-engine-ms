package io.segmentme.measurement.service.domain.statistic;

import io.segmentme.core.domain.DbDomain;
import lombok.Data;

import java.util.List;

@Data
public class AnalysisCount implements DbDomain {
    private List<AggregatedAnalysisCount> result;

}

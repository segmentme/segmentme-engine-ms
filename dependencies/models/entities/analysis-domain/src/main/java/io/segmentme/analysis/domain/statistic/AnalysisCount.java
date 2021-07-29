package io.segmentme.analysis.domain.statistic;

import lombok.Data;

import java.util.List;

@Data
public class AnalysisCount {
    private List<AggregatedAnalysisCount> result;

}

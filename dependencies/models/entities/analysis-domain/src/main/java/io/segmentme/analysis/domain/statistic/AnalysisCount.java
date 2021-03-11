package io.segmentme.analysis.domain.statistic;

import io.segmentme.models.shared.analysis.AggregatedAnalysisCount;
import lombok.Data;

import java.util.List;

@Data
public class AnalysisCount {
    private List<AggregatedAnalysisCount> result;

}

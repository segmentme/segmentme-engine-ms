package io.segmentme.measurement.dto.statistic;

import io.segmentme.measurement.dto.analysis.SegmentShortInfo;
import io.segmentme.models.shared.analysis.AggregatedAnalysisCount;
import io.segmentme.models.shared.analysis.IntegrationPoint;
import lombok.Data;

import java.util.List;

@Data
public class DashboardData {
    private List<IntegrationPoint> integrationPoints;

    private List<SegmentShortInfo> segments;

    private List<AggregatedAnalysisCount> analysisCount;
}

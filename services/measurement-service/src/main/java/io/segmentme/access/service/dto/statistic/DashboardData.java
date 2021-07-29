package io.segmentme.access.service.dto.statistic;

import io.segmentme.access.service.domain.statistic.AggregatedAnalysisCount;
import io.segmentme.access.service.dto.analysis.SegmentShortInfo;
import io.segmentme.models.shared.analysis.IntegrationPoint;
import lombok.Data;

import java.util.List;

@Data
public class DashboardData {
    private List<IntegrationPoint> integrationPoints;

    private List<SegmentShortInfo> segments;

    private List<AggregatedAnalysisCount> analysisCount;
}

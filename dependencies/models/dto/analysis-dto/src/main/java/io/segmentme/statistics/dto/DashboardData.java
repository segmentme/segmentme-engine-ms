package io.segmentme.statistics.dto;

import io.segmentme.analysis.dto.segment.SegmentShortInfo;
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

package io.segmentme.measurement.service.dto.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor(staticName = "of")
public class ExploreListView {

    private StatisticShortInfo statistic;

    private List<StatisticSegmentInfo> segments;

}

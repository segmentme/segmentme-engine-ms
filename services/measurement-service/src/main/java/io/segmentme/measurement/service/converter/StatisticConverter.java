package io.segmentme.measurement.service.converter;

import io.segmentme.measurement.service.domain.statistic.StatisticLog;
import io.segmentme.measurement.service.dto.statistic.StatisticShortInfo;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class StatisticConverter {

    public StatisticShortInfo of(StatisticLog statisticLog) {
        return new StatisticShortInfo()
            .setId(statisticLog.getId())
            .setWorkspaceId(statisticLog.getWorkspaceId())
            .setAnalysisTime(statisticLog.getAnalysisTime())
            .setDateTime(statisticLog.getLastModifiedDate())
            .setIntegrationPointKey(statisticLog.getIntegrationPointKey())
            .setSegmentCount(statisticLog.getSegmentStatistics().size());
    }

    public List<StatisticShortInfo> of(List<StatisticLog> statisticLog) {
        if (statisticLog == null) {
            return new ArrayList<>();
        }

        return statisticLog.stream().map(StatisticConverter::of).collect(Collectors.toList());
    }
}


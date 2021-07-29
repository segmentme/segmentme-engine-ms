package io.segmentme.access.service.resource;

import io.segmentme.access.service.converter.SegmentShortInfoConverter;
import io.segmentme.access.service.converter.StatisticConverter;
import io.segmentme.access.service.domain.segment.Segment;
import io.segmentme.access.service.domain.statistic.SegmentStatisticCount;
import io.segmentme.access.service.domain.statistic.StatisticLog;
import io.segmentme.access.service.dto.statistic.DashboardData;
import io.segmentme.access.service.dto.statistic.ExploreListView;
import io.segmentme.access.service.dto.statistic.StatisticSegmentInfo;
import io.segmentme.access.service.service.SegmentService;
import io.segmentme.access.service.service.StatisticService;
import io.segmentme.access.service.service.WorkspaceService;
import io.segmentme.models.shared.analysis.IntegrationPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/statistic")
@RequiredArgsConstructor
public class StatisticController {
    private final StatisticService statisticService;
    private final WorkspaceService workspaceFacade;
    private final SegmentService segmentService;


    @PreAuthorize("@workspaceSecurityService.isWorkspaceMember(#workspaceId)")
    @GetMapping("/{workspaceId}/segment-statistic-count")
    public List<SegmentStatisticCount> getSegmentStatistic(@PathVariable String workspaceId, @RequestParam int period) {
        return statisticService.getSegmentStatistic(workspaceId, period);
    }


    @PreAuthorize("@workspaceSecurityService.isWorkspaceMember(#workspaceId)")
    @GetMapping("/{workspaceId}/segment-statistic-criteria-count")
    public DashboardData getStatisticCriteriaCount(@PathVariable String workspaceId, @RequestParam int period,
                                                   @RequestParam String criteria, @RequestParam String value) {
        List<IntegrationPoint> integrationPoints = Objects.requireNonNull(workspaceFacade.findById(workspaceId).orElse(null)).getIntegrationPoints();
        return new DashboardData()
            .setAnalysisCount(statisticService.getAnalysisCountForCriteriaValue(workspaceId, period, criteria, value))
            .setIntegrationPoints(integrationPoints);
    }


    @PreAuthorize("@workspaceSecurityService.isWorkspaceMember(#workspaceId)")
    @GetMapping("/{workspaceId}/segment-statistic-view")
    public Page<ExploreListView> getStatistics(@PathVariable String workspaceId,
                                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                               @RequestParam String criteria, @RequestParam String value, Pageable pageable) {

        var statistics = statisticService.getSegmentStatistic(workspaceId, startDate, endDate, criteria, value, pageable).getContent();

        var segments = segmentService.findByIds(statistics.stream()
            .map(StatisticLog::getSegmentStatistics)
            .flatMap(Collection::stream)
            .map(StatisticLog.SegmentStatistic::getSegmentId)
            .collect(Collectors.toSet()))
            .stream()
            .collect(Collectors.toMap(Segment::getId, Function.identity()));

        return statisticService.getSegmentStatistic(workspaceId, startDate, endDate, criteria, value, pageable)
            .map(statistic -> {
                var statisticSegmentInfos = statistic.getSegmentStatistics().stream()
                    .map(it -> of(segments.get(it.getSegmentId()), it))
                    .collect(Collectors.toList());
                return ExploreListView.of(StatisticConverter.of(statistic), statisticSegmentInfos);
            });
    }


    @PreAuthorize("@workspaceSecurityService.isWorkspaceMember(#workspaceId)")
    @GetMapping("/{workspaceId}/statistic-log-details/{statisticLogId}")
    public StatisticService.ExploreStatisticLog getStatisticLog(@PathVariable String workspaceId, @PathVariable String statisticLogId) {
        return statisticService.getStatisticLogOverview(statisticLogId);
    }

    @PreAuthorize("@workspaceSecurityService.isWorkspaceMember(#workspaceId)")
    @GetMapping("/{workspaceId}/total-analysis-count")
    public DashboardData getTotalAnalyticsCount(@PathVariable String workspaceId, @RequestParam int period) {
        List<IntegrationPoint> integrationPoints = Objects.requireNonNull(workspaceFacade.findById(workspaceId).orElse(null)).getIntegrationPoints();
        return new DashboardData()
            .setAnalysisCount(statisticService.getAnalysisCount(workspaceId, period))
            .setSegments(integrationPoints.stream().map(it -> segmentService.findByIntegrationPointKey(it.getKey())).flatMap(Collection::stream)
                .map(SegmentShortInfoConverter::of).collect(Collectors.toList()))
            .setIntegrationPoints(integrationPoints);
    }


    private StatisticSegmentInfo of(Segment segment, StatisticLog.SegmentStatistic segmentStatistic) {
        return new StatisticSegmentInfo()
            .setId(segment.getId())
            .setName(segment.getName())
            .setAnalysisTime(segmentStatistic.getAnalysisTime())
            .setResult(segmentStatistic.isAnalysisResult());
    }
}

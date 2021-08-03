package io.segmentme.measurement.service.service;

import io.segmentme.measurement.service.domain.statistic.AnalyzedData;
import io.segmentme.measurement.service.domain.statistic.ParticipantStatistic;
import io.segmentme.measurement.service.domain.statistic.StatisticLog;
import io.segmentme.measurement.service.dto.analysis.CollectedAnalysysStatisticDto;
import io.segmentme.measurement.service.dto.analysis.SegmentAnalysisResult;
import io.segmentme.measurement.service.dto.analysis.SegmentDto;
import io.segmentme.measurement.service.dto.analysis.conditions.SegmentConditionDto;
import io.segmentme.models.shared.analysis.ConditionType;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Component
@RequiredArgsConstructor
@Slf4j
public class StatisticManager {

    private final StatisticService statisticService;

    private final SegmentService segmentService;
    private final ParticipantStatisticService participantStatisticService;

    private final AnalyzedDataService analyzedDataService;
    private List<StatisticLog> statisticLogs = Collections.synchronizedList(new ArrayList<>());
    private List<AnalyzedData> analyzedDatas = Collections.synchronizedList(new ArrayList<>());


    public void saveStatistic(CollectedAnalysysStatisticDto collectedStatistic) {
        AnalyzedData analyzedData = aggregateAnalyzedData(collectedStatistic);

        StatisticLog statisticLog = new StatisticLog();
        statisticLog.setWorkspaceId(collectedStatistic.getWorkspaceId());
        statisticLog.setAnalysisTime(collectedStatistic.getAnalysisTime());
        statisticLog.setIntegrationPointKey(collectedStatistic.getIntegrationPointKey());
        statisticLog.setAnalyzedDataKey(analyzedData.getHash());
        statisticLog.setKnownTypes(statisticLog.getKnownTypes());
        statisticLog.setSegmentStatistics(getSegmentStatistics(collectedStatistic));
        statisticLog.setConditionStatistics(getConditionsBreakdown(collectedStatistic));

        batchSave(analyzedData, statisticLog, collectedStatistic);
    }

    private void batchSave(AnalyzedData analyzedData, StatisticLog statisticLog, CollectedAnalysysStatisticDto collectedStats) {
        log.info("Add data to batch. statistic log batch size:{}, analyzedData batch size: {}", statisticLogs.size(), analyzedDatas.size());
        statisticLog.setCreatedDate(Instant.now());
        analyzedData.setCreatedDate(Instant.now());
        statisticLogs.add(statisticLog);
        analyzedDatas.removeIf(it -> it.getHash().equals(analyzedData.getHash()));
        analyzedDatas.add(analyzedData);
        CollectedAnalysysStatisticDto.ContextDataHolder contextDataHolder = collectedStats.getContextDataHolder();

        updateParticipantStatistics(analyzedData, statisticLog, contextDataHolder, collectedStats.getTimestamp());

        if (RandomUtils.nextInt(0, 50) == 0) {
            flush();
        }
    }

    private void updateParticipantStatistics(AnalyzedData analyzedData, StatisticLog statisticLog, CollectedAnalysysStatisticDto.ContextDataHolder contextDataHolder, Long timestamp) {
        Object participantIdentifier = analyzedData.getNodeValues().get(contextDataHolder.getUniquenessIndicator()).get(0);
        ParticipantStatistic participant = new ParticipantStatistic();
        participant.setContextId(contextDataHolder.getContextId());
        participant.setLastSegmentStatistic(statisticLog.getSegmentStatistics());
        participant.setUniquenessIndicator(contextDataHolder.getUniquenessIndicator());
        participant.setUniquenessValue(participantIdentifier);
        participantStatisticService.update(participant);
    }


    public void flush() {
        statisticService.update(statisticLogs);
        log.info("Saved {} statisticLogs", statisticLogs.size());
        analyzedDataService.save(analyzedDatas);
        log.info("Saved {} analyzed data", statisticLogs.size());
        statisticLogs.clear();
        analyzedDatas.clear();
    }

    private AnalyzedData aggregateAnalyzedData(CollectedAnalysysStatisticDto collectedStatistic) {
        AnalyzedData analyzedData = new AnalyzedData();
        analyzedData.setWorkspaceId(collectedStatistic.getWorkspaceId());
        analyzedData.setPayload(collectedStatistic.getRawPayload().toString());
        analyzedData.setNodeValues(prepareNodeValues(collectedStatistic.getContextDataHolder().getValues()));
        analyzedData.setClientId(collectedStatistic.getClientId());
        analyzedData.setIntegrationPointKey(collectedStatistic.getIntegrationPointKey());
        analyzedData.setContextId(collectedStatistic.getContextDataHolder().getContextId());
        analyzedData.setAnalyzedSegments(collectedStatistic.getAnalyzedSegments()
            .stream()
            .map(SegmentDto::getId).collect(Collectors.toList()));
        analyzedData.setHash(analyzedData.buildHash());
        return analyzedData;
    }

    private Map<String, List<Object>> prepareNodeValues(Map<String, Object> values) {
        Map<String, List<Object>> prepared = new HashMap<>();

        values.forEach((key, value) -> {
            List<Object> preparedValue = null;
            if (value == null) {
                return;
            }
            if (List.class.isAssignableFrom(value.getClass())) {
                preparedValue = (List<Object>) value;
            } else {
                preparedValue = new ArrayList<>();
                preparedValue.add(value);
            }
            prepared.put(key, preparedValue);
        });
        return prepared;
    }

    private List<StatisticLog.SegmentStatistic> getSegmentStatistics(CollectedAnalysysStatisticDto collectedStatistic) {
        return collectedStatistic.getAnalyzedSegments().stream()
            .map(it -> {
                SegmentAnalysisResult segmentAnalysisResult = collectedStatistic.getSegmentAnalysisResults()
                    .stream()
                    .filter(result -> result.getHash().equalsIgnoreCase(it.getHash()))
                    .findFirst()
                    .get();
                return new StatisticLog.SegmentStatistic().setSegmentId(it.getId())
                    .setAnalysisResult(segmentAnalysisResult.isValue())
                    .setAnalysisTime(segmentAnalysisResult.getAnalysisTime())
                    .setConditionsHash(getSegmentConditions(it, new HashMap<>()));
            })
            .collect(Collectors.toList());
    }

    private List<StatisticLog.ConditionStatistic> getConditionsBreakdown(CollectedAnalysysStatisticDto collectedStatistic) {
        return collectedStatistic.getConditionResults().entrySet().stream()
            .map(it -> new StatisticLog.ConditionStatistic()
                .setHash(String.valueOf(it.getKey()))
                .setCriteria(it.getValue().getCriteria())
                .setErrors(it.getValue().getErrors()))
            .collect(Collectors.toList());
    }


    private Map<String, Integer> getSegmentConditions(SegmentDto segment, Map<String, Integer> conditions) {

        segment.getConditions().stream()
            .peek(it -> {
                if (it.getType() == ConditionType.SEGMENT) {
                    getSegmentConditions(((SegmentConditionDto) it).getValue(), conditions);
                }
            })
            .peek(it -> conditions.putIfAbsent(String.valueOf(it.hashCode()), 0))
            .forEach(it -> conditions.computeIfPresent(String.valueOf(it.hashCode()), (s, integer) -> ++integer));

        return conditions;
    }

}

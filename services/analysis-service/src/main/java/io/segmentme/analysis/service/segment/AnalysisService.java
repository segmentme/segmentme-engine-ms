package io.segmentme.analysis.service.segment;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.analysis.dto.AnalysisData;
import io.segmentme.analysis.dto.AnalysisResult;
import io.segmentme.analysis.dto.CollectedAnalysysStatisticDto;
import io.segmentme.analysis.dto.SegmentAnalysisResult;
import io.segmentme.analysis.service.converter.RedisMessageOutConverter;
import io.segmentme.analysis.service.converter.SegmentConverter;
import io.segmentme.analysis.service.exception.AnalysisException;
import io.segmentme.analysis.service.segment.worm.DebugWorm;
import io.segmentme.analysis.service.segment.worm.StatisticWorm;
import io.segmentme.analysis.service.segment.worm.Worm;
import io.segmentme.analysis.service.segment.worm.WormConsumer;
import io.segmentme.core.domain.context.ContextSchema;
import io.segmentme.core.domain.segment.Segment;
import io.segmentme.core.domain.workpsace.Workspace;
import io.segmentme.helpers.context.processor.ContextValueHolder;
import io.segmentme.helpers.context.processor.ContextValuesExtractor;
import io.segmentme.helpers.dao.repository.SegmentRepository;
import io.segmentme.helpers.dao.service.ContextSchemaService;
import io.segmentme.helpers.dao.service.WorkspaceService;
import io.segmentme.redis.config.MessagePublisher;
import io.segmentme.redis.dto.AnalysisRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.segmentme.analysis.service.exception.error.AnalysisErrors.INTEGRATION_POINT_NOT_FOUND;
import static io.segmentme.redis.config.RedisTopicsBuilder.buildAnalysisResponseTopic;


@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final MessagePublisher messagePublisher;

    private final SegmentAnalysisService segmentAnalysisService;

    private final SegmentRepository analysisRuleRepository;

    private final ContextSchemaService contextSchemaService;

    private final ContextValuesExtractor contextValuesExtractor;

    private final WorkspaceService workspaceService;

    private final ApplicationEventPublisher applicationEventPublisher;


    public AnalysisResult debug(ContextValueHolder context, String segmentId) {
        return debug(context, analysisRuleRepository.findById(segmentId).orElse(null));
    }

    public AnalysisResult debug(ContextValueHolder context, Segment segment) {

        DebugWorm worm = new DebugWorm(context);
        SegmentAnalysisResult result = analyze(context, segment, WormConsumer.of(Collections.singletonList(worm)));

        return AnalysisResult.of(Collections.singletonList(result), worm.getDebugResultMap());
    }

    public AnalysisResult debug(String integrationPointKey, String contextId, JsonNode payload, Segment segment) {
        ContextSchema schema = contextSchemaService.findByIdAndIntegrationPointKey(contextId, integrationPointKey)
            .orElseThrow(() -> new AnalysisException().setCode(INTEGRATION_POINT_NOT_FOUND));

        Workspace workspace = workspaceService.findByIntegrationPointKey(integrationPointKey)
            .orElseThrow(() -> new IllegalArgumentException("Workspace not found"));

        return debug(contextValuesExtractor.extractValues(payload, schema, workspace.getConfiguration()), segment);
    }

    public List<SegmentAnalysisResult> analyze(ContextValueHolder context, List<Segment> rules, StatisticWorm statisticWorm) {
        WormConsumer worm = WormConsumer.of(Collections.singletonList(statisticWorm));
        return rules.stream().map(it -> this.analyze(context, it, worm)).collect(Collectors.toList());
    }

    public List<SegmentAnalysisResult> analyze(String integrationPointKey, AnalysisData analysisData, Segment segment) {
        return analyze(null, integrationPointKey, analysisData, List.of(segment));
    }

    public List<SegmentAnalysisResult> analyze(String integrationPointKey, String contextId, AnalysisData analysisData) {
        return analyze(contextId, integrationPointKey, analysisData, analysisRuleRepository.findByIntegrationPointKeyAndActive(integrationPointKey, true));
    }

    private List<SegmentAnalysisResult> analyze(String contextId, String integrationPointKey, AnalysisData analysisData, List<Segment> segments) {
        CollectedAnalysysStatisticDto statisticLogEntry = new CollectedAnalysysStatisticDto();
        statisticLogEntry.setRawPayload(analysisData.getPayload());
        statisticLogEntry.setClientId(analysisData.getClientId());

        StatisticWorm worm = new StatisticWorm();
        long analyzeStartTime = System.currentTimeMillis();

        statisticLogEntry.setIntegrationPointKey(integrationPointKey);
        Workspace workspace = workspaceService.findByIntegrationPointKey(integrationPointKey).orElseThrow(() -> new IllegalArgumentException("Workspace not found"));
        statisticLogEntry.setWorkspaceId(workspace.getId());
        List<ContextSchema> schemas = contextSchemaService.findByIntegrationPointKeys(Collections.singletonList(integrationPointKey), false);

        if (StringUtils.isNoneBlank(contextId)) {
            schemas = schemas.stream().filter(it -> it.getId().equalsIgnoreCase(contextId)).findFirst().map(Collections::singletonList).orElseThrow(() -> new IllegalArgumentException("Context not found"));
            segments = segments.stream().filter(it -> it.getContextId().equalsIgnoreCase(contextId)).collect(Collectors.toList());
        } else {
            schemas = new ArrayList<>();
            schemas.add(null);
        }

        List<Segment> finalSegments = segments;
        try {
            List<SegmentAnalysisResult> segmentAnalysisResults = schemas.stream()
                .map(it -> contextValuesExtractor.extractValues(analysisData.getPayload(), it, workspace.getConfiguration()))
                .peek(contextValueHolder -> statisticLogEntry.setContextDataHolder(convertToStatisticContextData(contextValueHolder)))
                .map(it -> this.analyze(it, finalSegments, worm))
                .flatMap(List::stream).collect(Collectors.toList());
            statisticLogEntry.setSegmentAnalysisResults(segmentAnalysisResults);
            return segmentAnalysisResults;
        } finally {
            statisticLogEntry.setAnalyzedSegments(segments.stream().map(SegmentConverter::of).collect(Collectors.toList()));
            statisticLogEntry.setConditionResults(worm.getStatisticMap());
            statisticLogEntry.setAnalysisTime(System.currentTimeMillis() - analyzeStartTime);
            applicationEventPublisher.publishEvent(statisticLogEntry);
        }
    }

    private CollectedAnalysysStatisticDto.ContextDataHolder convertToStatisticContextData(ContextValueHolder contextValueHolder) {
        return new CollectedAnalysysStatisticDto.ContextDataHolder()
            .setValues(contextValueHolder.getValues())
            .setKnownTypes(Optional.ofNullable(contextValueHolder.getSchema()).map(ContextSchema::getInlinePath).orElse(null))
            .setExtractedValues(contextValueHolder.getExtractedValues());
    }

    @SuppressWarnings({"unchecked"})
    private SegmentAnalysisResult analyze(ContextValueHolder context, Segment rule, Worm<?> worm) {
        long startTime = System.currentTimeMillis();
        SegmentAnalysisResult analyze = segmentAnalysisService.analyze(context, rule, (Worm<Object>) worm);
        analyze.setAnalysisTime(System.currentTimeMillis() - startTime);
        return analyze;
    }

    public void analyseRedisMessage(AnalysisRequest request) {
        AnalysisRequest.AnalysisData analysisData = request.getAnalysisData();

        ChannelTopic analysisResultTopic = buildAnalysisResponseTopic(request.getIntegrationPointKey(), request.getContextKey(), analysisData.getClientId());

        //todo [vk]: analysis service should not call schema actualizer. api or channel service should do it before sending to analysis.
        AnalysisResult response = AnalysisResult.of(this.analyze(request.getIntegrationPointKey(), request.getContextId(), new AnalysisData().setClientId(analysisData.getClientId()).setPayload(analysisData.getPayload())), null);

        messagePublisher.publish(RedisMessageOutConverter.of(response), analysisResultTopic.getTopic());
    }
}

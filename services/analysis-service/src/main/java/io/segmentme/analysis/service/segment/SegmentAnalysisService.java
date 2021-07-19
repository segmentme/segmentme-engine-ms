package io.segmentme.analysis.service.segment;

import io.segmentme.analysis.dto.SegmentAnalysisResult;
import io.segmentme.analysis.service.condition.ConditionMatcher;
import io.segmentme.analysis.service.segment.worm.Worm;
import io.segmentme.core.domain.condition.AbstractCondition;
import io.segmentme.core.domain.segment.Segment;
import io.segmentme.helpers.context.processor.ContextValueHolder;
import io.segmentme.statistics.dto.ParticipantStatisticDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SegmentAnalysisService {

    private final static String PARTICIPANT_PATH = "/participant/{contextId}";
    private final static String PARTICIPANT_PATH_UNIQUENESS_VALUE = "uniquenessValue";

    @Qualifier("measurement-service")
    private final WebClient measurementServiceWebClient;

    private final ConditionMatcher conditionMatcher;

    public SegmentAnalysisResult analyze(ContextValueHolder context, Segment segment, Worm<Object> worm) {
        var segmentValue = this.getSegmentValueIfSatisfy(context, segment, worm);
        var isOpenFor = isOpenFor(context, segment, segmentValue);
        return SegmentAnalysisResult.of(segment.getName(), segment.getId(), segment.getHash(), segmentValue, isOpenFor, 0L);
    }

    final boolean isMatch(Segment segment, ContextValueHolder context, Worm<Object> worm) {
        if (CollectionUtils.isEmpty(segment.getConditions())) {
            return true;
        }

        return switch (segment.getAggregation()) {
            case OR -> segment.getConditions().stream().anyMatch(condition -> match(condition, context, worm));
            case AND -> segment.getConditions().stream().allMatch(condition -> match(condition, context, worm));
        };
    }

    private boolean getSegmentValueIfSatisfy(ContextValueHolder context, Segment segment, Worm<Object> worm) {
        return isMatch(segment, context, worm) == segment.isMatchResult();
    }

    private boolean match(AbstractCondition condition, ContextValueHolder context, Worm<Object> worm) {
        return conditionMatcher.match(condition, context, worm);
    }

    private boolean isOpenFor(ContextValueHolder context, Segment segment, boolean segmentValue) {
        String uniquenessIndicator = context.getSchema().getUniquenessIndicator();

        if (segmentValue && segment.getOpenedForPercentage() < 100 && Objects.nonNull(segment.getId()) && uniquenessIndicator != null) {
            var participantStatistic = findParticipantStatistic(context.getSchema().getId(), context.getValues().get(uniquenessIndicator));

            if (participantStatistic.isEmpty()) {
                return true;
            }

            return participantStatistic
                    .map(ParticipantStatisticDto::getInSegment)
                    .filter(it -> CollectionUtils.isNotEmpty(it) && it.contains(segment.getId()))
                    .isPresent();
        }
        return segmentValue;
    }

    private Optional<ParticipantStatisticDto> findParticipantStatistic(String contextId, Object uniquenessValue) {
        return Optional.ofNullable(measurementServiceWebClient.get()
                .uri(builder -> builder.path(PARTICIPANT_PATH).queryParam(PARTICIPANT_PATH_UNIQUENESS_VALUE, uniquenessValue).build(contextId))
                .exchangeToMono(result -> result.statusCode().isError() ? result.createException().flatMap(Mono::error) : result.bodyToMono(ParticipantStatisticDto.class))
                .block());
    }
}

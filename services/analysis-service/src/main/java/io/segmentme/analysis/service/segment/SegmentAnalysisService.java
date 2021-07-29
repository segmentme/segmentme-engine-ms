package io.segmentme.analysis.service.segment;

import io.segmentme.analysis.domain.condition.AbstractCondition;
import io.segmentme.analysis.domain.context.ContextSchema;
import io.segmentme.analysis.domain.segment.Segment;
import io.segmentme.analysis.dto.SegmentAnalysisResult;
import io.segmentme.analysis.service.clients.MeasurementClient;
import io.segmentme.analysis.service.clients.dto.ParticipantStatisticDto;
import io.segmentme.analysis.service.condition.ConditionMatcher;
import io.segmentme.analysis.service.segment.worm.Worm;
import io.segmentme.helpers.context.processor.ContextValueHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class SegmentAnalysisService {

    private final MeasurementClient measurementClient;

    private final ConditionMatcher conditionMatcher;

    public SegmentAnalysisResult analyze(ContextValueHolder<ContextSchema> context, Segment segment, Worm<Object> worm) {
        var segmentValue = this.getSegmentValueIfSatisfy(context, segment, worm);
        var isOpenFor = isOpenFor(context, segment, segmentValue);
        return SegmentAnalysisResult.of(segment.getName(), segment.getId(), segment.getHash(), segmentValue, isOpenFor, 0L);
    }

    final boolean isMatch(Segment segment, ContextValueHolder<ContextSchema> context, Worm<Object> worm) {
        if (CollectionUtils.isEmpty(segment.getConditions())) {
            return true;
        }

        return switch (segment.getAggregation()) {
            case OR -> segment.getConditions().stream().anyMatch(condition -> match(condition, context, worm));
            case AND -> segment.getConditions().stream().allMatch(condition -> match(condition, context, worm));
        };
    }

    private boolean getSegmentValueIfSatisfy(ContextValueHolder<ContextSchema> context, Segment segment, Worm<Object> worm) {
        return isMatch(segment, context, worm) == segment.isMatchResult();
    }

    private boolean match(AbstractCondition condition, ContextValueHolder<ContextSchema> context, Worm<Object> worm) {
        return conditionMatcher.match(condition, context, worm);
    }

    private boolean isOpenFor(ContextValueHolder<ContextSchema> context, Segment segment, boolean segmentValue) {
        ContextSchema schema = context.getSchema();
        String uniquenessIndicator = schema.getUniquenessIndicator();

        if (segmentValue && segment.getOpenedForPercentage() < 100 && Objects.nonNull(segment.getId()) && uniquenessIndicator != null) {
            var participantStatistic = measurementClient.findParticipantStatistic(schema.getId(), context.getValues().get(uniquenessIndicator));

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


}

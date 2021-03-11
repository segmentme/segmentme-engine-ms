package io.segmentme.analysis.service.condition;

import io.segmentme.analysis.service.ContextValueHolder;
import io.segmentme.analysis.service.segment.SegmentAnalysisService;
import io.segmentme.analysis.service.segment.worm.Worm;
import io.segmentme.core.domain.condition.SegmentCondition;
import io.segmentme.helpers.dao.repository.SegmentRepository;
import io.segmentme.models.shared.analysis.ConditionType;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Data
@Component
@RequiredArgsConstructor
public class SegmentConditionMatcher implements Matcher<SegmentCondition> {

    private final ConditionType type = ConditionType.SEGMENT;

    private final SegmentRepository analysisRuleRepository;

    @Lazy
    private final ConditionMatcher conditionMatcher;

    @Lazy
    private final SegmentAnalysisService analysisRuleService;


    @Override
    public boolean match(SegmentCondition condition, ContextValueHolder context, Worm<Object> worm) {
        return worm.computeResult(condition.getHash(), matchFunction(condition, context, worm)) == condition.isMatchResult();
    }

    private Function<String, Boolean> matchFunction(SegmentCondition condition, ContextValueHolder context, Worm<Object> worm) {
        return hash -> {
            var conditionMatchResult = analysisRuleService.analyze(context, condition.getValue(), worm).isValue();
            worm.apply(condition, conditionMatchResult);
            return conditionMatchResult;
        };
    }
}

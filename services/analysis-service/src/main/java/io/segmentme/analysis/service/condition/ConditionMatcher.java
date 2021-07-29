package io.segmentme.analysis.service.condition;

import io.segmentme.analysis.domain.condition.AbstractCondition;
import io.segmentme.analysis.service.segment.worm.Worm;
import io.segmentme.helpers.context.processor.ContextValueHolder;
import io.segmentme.models.shared.analysis.ConditionType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ConditionMatcher {

    private final Map<ConditionType, Matcher<? extends AbstractCondition>> conditionServices;

    public ConditionMatcher(List<Matcher<? extends AbstractCondition>> services) {
        conditionServices = services.stream().collect(Collectors.toMap(Matcher::getType, it -> it));
    }

    public boolean match(AbstractCondition condition, ContextValueHolder context, Worm<Object> worm) {
        return worm.computeResult(condition.getHash(), matchFunction(condition, context, worm)) == condition.isMatchResult();
    }

    private Function<String, Boolean> matchFunction(AbstractCondition condition, ContextValueHolder context, Worm<Object> worm) {
        return hash -> {
            var matcher = findMatcher(condition.getType());
            var conditionMatchResult = matcher.match(condition, context, worm);
            worm.apply(condition, conditionMatchResult);
            return conditionMatchResult;
        };
    }

    @SuppressWarnings("unchecked")
    private Matcher<AbstractCondition> findMatcher(ConditionType type) {
        Matcher<? extends AbstractCondition> matcher = conditionServices.computeIfAbsent(type, key -> {
            throw new IllegalStateException("Unknown condition service type " + key);
        });
        return (Matcher<AbstractCondition>) matcher;
    }
}

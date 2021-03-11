package io.segmentme.analysis.service.condition;

import io.segmentme.core.domain.condition.SingleCondition;
import io.segmentme.models.shared.analysis.ConditionType;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
class LteConditionMatcher extends SimpleConditionMatcher<SingleCondition> {

    private final ConditionType type = ConditionType.LTE;

    @Override
    boolean match(Comparable<Object> expected, Comparable<Object> actual) {
        return actual.compareTo(expected) <= 0;
    }
}

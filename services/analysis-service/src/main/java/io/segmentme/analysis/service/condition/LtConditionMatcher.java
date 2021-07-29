package io.segmentme.analysis.service.condition;

import io.segmentme.analysis.domain.condition.SingleCondition;
import io.segmentme.models.shared.analysis.ConditionType;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
class LtConditionMatcher extends SimpleConditionMatcher<SingleCondition> {

    private final ConditionType type = ConditionType.LT;

    @Override
    boolean match(Comparable<Object> expected, Comparable<Object> actual) {
        return actual.compareTo(expected) < 0;
    }
}

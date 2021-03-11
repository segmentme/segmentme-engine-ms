package io.segmentme.analysis.service.condition;

import io.segmentme.core.domain.condition.SingleCondition;
import io.segmentme.models.shared.analysis.ConditionType;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
class GtConditionMatcher extends SimpleConditionMatcher<SingleCondition> {

    private final ConditionType type = ConditionType.GT;


    @Override
    boolean match(Comparable<Object> expected, Comparable<Object> actual) {
        return actual.compareTo(expected) > 0;
    }
}

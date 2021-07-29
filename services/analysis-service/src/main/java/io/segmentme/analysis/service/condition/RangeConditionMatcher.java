package io.segmentme.analysis.service.condition;

import io.segmentme.analysis.domain.condition.RangeCondition;
import io.segmentme.models.shared.analysis.ConditionType;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
class RangeConditionMatcher extends SimpleConditionMatcher<RangeCondition> {

    private final ConditionType type = ConditionType.RANGE;

    @Override
    protected Comparable<Object> getExpectedValue(RangeCondition condition, Comparable<Object> actualValue) {
        RangeValue rangeValue = condition.getValue();
        Comparable<Object> first = castJsonProperty(rangeValue.getMin(), actualValue);
        Comparable<Object> last = castJsonProperty(rangeValue.getMax(), actualValue);
        return new RangeValue().setMin(first).setMax(last);
    }

    @Override
    boolean match(Comparable<Object> expected, Comparable<Object> actual) {
        RangeValue rangeValue = (RangeValue) expected;
        return rangeValue.compareTo(actual) == 0;
    }
}

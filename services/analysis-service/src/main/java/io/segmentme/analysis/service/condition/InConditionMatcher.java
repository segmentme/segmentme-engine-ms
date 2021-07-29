package io.segmentme.analysis.service.condition;

import io.segmentme.analysis.domain.condition.ArrayCondition;
import io.segmentme.models.shared.analysis.ConditionType;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Getter
@Service
class InConditionMatcher extends AbstractConditionMatcher<ArrayCondition, List<Object>, Comparable<Object>> {

    private final ConditionType type = ConditionType.IN;


    @Override
    protected List<Object> getExpectedValue(ArrayCondition condition, Comparable<Object> actualValue) {
        return condition.getValue();
    }

    @Override
    Optional<Boolean> checkForNullValid(ArrayCondition condition, Comparable<Object> value) {
        return Optional.ofNullable(value != null ? null : condition.isNullValid());
    }

    @Override
    boolean match(List<Object> expected, Comparable<Object> actual) {
        return expected.stream().map(it -> castJsonProperty(it, actual)).anyMatch(it -> it.compareTo(actual) == 0);
    }
}

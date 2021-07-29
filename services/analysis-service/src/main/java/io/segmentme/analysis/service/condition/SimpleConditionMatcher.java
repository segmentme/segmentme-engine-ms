package io.segmentme.analysis.service.condition;

import io.segmentme.analysis.domain.condition.SimpleCondition;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
abstract class SimpleConditionMatcher<T extends SimpleCondition<?>> extends AbstractConditionMatcher<T, Comparable<Object>, Comparable<Object>> {

    @Override
    Optional<Boolean> checkForNullValid(T condition, Comparable<Object> value) {
        return Optional.ofNullable(value != null ? null : condition.isNullValid());
    }


    @Override
    protected Comparable<Object> getExpectedValue(T condition, Comparable<Object> actualValue) {
        return castJsonProperty(condition.getValue(), actualValue);
    }
}

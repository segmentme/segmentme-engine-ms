package io.segmentme.analysis.service.condition;

import io.segmentme.models.shared.analysis.ConditionType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Data
@Slf4j
@Component
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class ContainsAnyConditionMatcher extends AbstractContainsConditionMatcher {

    private final ConditionType type = ConditionType.CONTAINS_ANY;


    @Override
    boolean match(Collection<Comparable<Object>> expected, Collection<Comparable<Object>> actual) {
        return actual.stream().anyMatch(it -> expected.stream().anyMatch(it::equals));
    }
}

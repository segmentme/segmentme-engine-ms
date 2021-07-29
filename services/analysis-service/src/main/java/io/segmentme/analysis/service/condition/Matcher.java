package io.segmentme.analysis.service.condition;

import io.segmentme.analysis.domain.condition.AbstractCondition;
import io.segmentme.analysis.domain.context.ContextSchema;
import io.segmentme.analysis.service.segment.worm.Worm;
import io.segmentme.helpers.context.processor.ContextValueHolder;
import io.segmentme.models.shared.analysis.ConditionType;

interface Matcher<T extends AbstractCondition> {

    boolean match(T condition, ContextValueHolder<ContextSchema> value, Worm<Object> worm);

    ConditionType getType();

}

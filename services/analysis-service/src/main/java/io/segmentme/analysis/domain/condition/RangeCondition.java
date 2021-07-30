package io.segmentme.analysis.domain.condition;

import io.segmentme.analysis.service.condition.RangeValue;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RangeCondition extends SimpleCondition<RangeValue> {


}

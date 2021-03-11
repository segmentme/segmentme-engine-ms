package  io.segmentme.core.domain.condition;

import io.segmentme.models.shared.analysis.RangeValue;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RangeCondition extends SimpleCondition<RangeValue> {


}

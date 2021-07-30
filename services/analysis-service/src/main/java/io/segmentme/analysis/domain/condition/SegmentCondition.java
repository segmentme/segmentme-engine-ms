package io.segmentme.analysis.domain.condition;

import io.segmentme.analysis.domain.segment.Segment;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SegmentCondition extends SimpleCondition<Segment> {

    @Override
    public void recalculateHash() {
        this.getValue().recalculateHash();
        super.recalculateHash();
    }
}

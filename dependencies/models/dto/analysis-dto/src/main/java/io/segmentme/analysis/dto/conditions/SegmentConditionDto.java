package io.segmentme.analysis.dto.conditions;

import io.segmentme.analysis.dto.segment.SegmentDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SegmentConditionDto extends SimpleConditionDto<SegmentDto> {
}

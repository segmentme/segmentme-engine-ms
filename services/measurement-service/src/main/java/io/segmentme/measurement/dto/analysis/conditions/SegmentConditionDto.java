package io.segmentme.measurement.dto.analysis.conditions;

import io.segmentme.measurement.dto.analysis.SegmentDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SegmentConditionDto extends SimpleConditionDto<SegmentDto> {
}

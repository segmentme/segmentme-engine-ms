package io.segmentme.analysis.dto.conditions;


import lombok.Data;

public class RangeConditionDto extends SimpleConditionDto<RangeConditionDto.RangeValue> {

    @Data
    public static class RangeValue {
        private Object min;

        private Object max;
    }
}

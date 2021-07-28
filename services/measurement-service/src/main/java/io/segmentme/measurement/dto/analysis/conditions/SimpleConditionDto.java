package io.segmentme.measurement.dto.analysis.conditions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class SimpleConditionDto<T> extends AbstractConditionDto {

    private T value;

    @JsonProperty(value = "isNullValid")
    private boolean isNullValid;

}

package io.segmentme.analysis.dto.segment;

import io.segmentme.analysis.dto.conditions.AbstractConditionDto;
import io.segmentme.models.shared.analysis.AggregationType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class SegmentDto {

    private String id;

    @NotNull
    private AggregationType aggregation;

    @NotBlank
    private String name;

    private String hash;

    private boolean active = true;

    private String description;

    private String integrationPointKey;

    @NotEmpty
    private List<AbstractConditionDto> conditions;

    private boolean matchResult;

}

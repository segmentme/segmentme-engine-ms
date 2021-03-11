package io.segmentme.analysis.dto.state;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.analysis.dto.segment.SegmentShortInfo;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class StateDto {

    private String id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String integrationPointKey;

    @NotNull
    private JsonNode value;

    private JsonNode defaultValue;

    @NotNull
    private SegmentShortInfo segment;

}

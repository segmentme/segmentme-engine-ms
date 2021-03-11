package io.segmentme.analysis.dto;

import io.segmentme.models.shared.analysis.InlineType;
import lombok.Data;

@Data
public class DebugResult {

    private String conditionId;

    private String conditionName;

    private boolean conditionMatchResult;

    private boolean finalMatchResult;

    private String criteria;

    private String segmentId;

    private String segmentName;

    private String errorMessage;

    private InlineType criteriaType;

    private Object value;
}

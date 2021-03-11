package io.segmentme.analysis.service.exception;

import io.segmentme.models.shared.exception.AbstractManagerException;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AnalysisException extends AbstractManagerException {
}

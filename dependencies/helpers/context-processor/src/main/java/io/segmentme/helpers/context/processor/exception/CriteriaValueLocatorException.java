package io.segmentme.helpers.context.processor.exception;

import io.segmentme.models.shared.exception.AbstractManagerException;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CriteriaValueLocatorException extends AbstractManagerException {
    private String criteria;

    public CriteriaValueLocatorException(String criteria, Throwable cause, String code) {
        super(cause, code);
        this.criteria = criteria;
    }
}

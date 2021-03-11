package io.segmentme.models.shared.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public abstract class AbstractManagerException extends RuntimeException implements Serializable {

    private SeverityLevel severity;

    private String code;

    protected AbstractManagerException() {
    }

    public AbstractManagerException(String message, String code) {
        super(message);
        setCode(code);
    }

    protected AbstractManagerException(String code) {
        setCode(code);
    }

    protected AbstractManagerException(Throwable cause, String code) {
        super(cause);
        setCode(code);
    }

    protected AbstractManagerException(Throwable cause) {
        super(cause);
    }

    public AbstractManagerException setCode(String code) {
        this.code = code;
        this.severity = Errors.getSeverity(code);
        return this;
    }
}

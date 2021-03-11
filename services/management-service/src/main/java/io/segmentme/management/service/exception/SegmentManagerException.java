package io.segmentme.management.service.exception;

import io.segmentme.models.shared.exception.AbstractManagerException;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class SegmentManagerException extends AbstractManagerException {

    public SegmentManagerException(String code) {
        super(code);
    }
}

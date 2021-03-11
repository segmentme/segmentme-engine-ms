package io.segmentme.management.service.exception.error;

import io.segmentme.models.shared.exception.Errors;

import java.util.Map;

import static io.segmentme.models.shared.exception.SeverityLevel.CRITICAL;
import static io.segmentme.models.shared.exception.SeverityLevel.MID;

public class SegmentMangerErrors implements Errors {

    public static final String DUPLICATED_SEGMENT_KEY = "segment.manager.duplicated.segment.key";
    public static final String SEGMENT_NOT_FOUND = "segment.not.found";

    static {
        ERRORS_SEVERITY.putAll(Map.of(
            DUPLICATED_SEGMENT_KEY, CRITICAL,
            SEGMENT_NOT_FOUND, MID
        ));
    }
}

package io.segmentme.models.shared.exception;

import java.util.HashMap;
import java.util.Map;

import static io.segmentme.models.shared.exception.SeverityLevel.MID;


public interface Errors {

    Map<String, SeverityLevel> ERRORS_SEVERITY = new HashMap<>();

    static SeverityLevel getSeverity(String error) {
        return ERRORS_SEVERITY.getOrDefault(error, MID);
    }
}

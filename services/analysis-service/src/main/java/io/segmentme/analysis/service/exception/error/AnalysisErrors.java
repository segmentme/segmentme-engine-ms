package io.segmentme.analysis.service.exception.error;

import io.segmentme.models.shared.exception.Errors;
import io.segmentme.models.shared.exception.SeverityLevel;

import java.util.Map;

public class AnalysisErrors implements Errors {

    public static final String INTEGRATION_POINT_NOT_FOUND = "analysis.error.integration.point.not.found";

    static {
        Errors.ERRORS_SEVERITY.putAll(Map.of(
            INTEGRATION_POINT_NOT_FOUND, SeverityLevel.CRITICAL
        ));
    }
}

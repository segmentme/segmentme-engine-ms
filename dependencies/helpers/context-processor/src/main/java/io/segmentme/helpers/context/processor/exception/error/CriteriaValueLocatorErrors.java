package io.segmentme.helpers.context.processor.exception.error;

import io.segmentme.models.shared.exception.Errors;
import io.segmentme.models.shared.exception.SeverityLevel;

import java.util.Map;

public class CriteriaValueLocatorErrors implements Errors {

    public static final String CRITERIA_NOT_FOUND = "criteria.value.locator.criteria.not.found";
    public static final String UNEXPECTED_LOCATOR_ERROR = "criteria.value.locator.unexpected.error";
    public static final String UNEXPECTED_ARRAY_TYPE = "criteria.value.locator.expected.single.but.found.array";

    static {
        Errors.ERRORS_SEVERITY.putAll(Map.of(
            CRITERIA_NOT_FOUND, SeverityLevel.MID,
            UNEXPECTED_LOCATOR_ERROR, SeverityLevel.MID,
            UNEXPECTED_ARRAY_TYPE, SeverityLevel.CRITICAL
        ));
    }
}

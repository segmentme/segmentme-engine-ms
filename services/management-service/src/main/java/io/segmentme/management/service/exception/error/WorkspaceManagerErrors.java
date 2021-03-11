package io.segmentme.management.service.exception.error;

import io.segmentme.models.shared.exception.Errors;

import java.util.Map;

import static io.segmentme.models.shared.exception.SeverityLevel.CRITICAL;

public class WorkspaceManagerErrors implements Errors {
    public static final String UNABLE_TO_DELETE_DEFAULT_WORKSAPCE = "workspace.manager.unable.to.delete.default.workspace";

    static {
        ERRORS_SEVERITY.putAll(Map.of(
            UNABLE_TO_DELETE_DEFAULT_WORKSAPCE, CRITICAL
        ));
    }
}

package io.segmentme.management.service.converter;

import io.segmentme.core.domain.workpsace.Workspace;
import io.segmentme.management.service.dto.WorkspaceHolder;

public class WorkspaceHolderConverter {

    private WorkspaceHolderConverter() {
    }

    public static WorkspaceHolder toHolder(Workspace workspace) {
        return new WorkspaceHolder()
                .setIntegrationPoints(workspace.getIntegrationPoints())
                .setWorkspaceConfiguration(workspace.getConfiguration())
                .setName(workspace.getName())
                .setId(workspace.getId());
    }
}

package io.segmentme.management.service.dto;

import io.segmentme.core.domain.workpsace.WorkspaceConfiguration;
import io.segmentme.models.shared.analysis.IntegrationPoint;
import lombok.Data;

import java.util.List;

@Data
public class WorkspaceHolder {
    private String id;

    private String name;

    private WorkspaceConfiguration workspaceConfiguration;

    private List<IntegrationPoint> integrationPoints;
}

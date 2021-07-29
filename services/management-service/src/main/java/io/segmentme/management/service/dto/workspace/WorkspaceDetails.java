package io.segmentme.management.service.dto.workspace;

import io.segmentme.management.service.domain.workpsace.WorkspaceConfiguration;
import io.segmentme.models.shared.analysis.IntegrationPoint;
import lombok.Data;

import java.util.List;

@Data
public class WorkspaceDetails {
    private String id;

    private String name;

    private WorkspaceConfiguration configuration;

    private List<IntegrationPoint> integrationPoints;

}

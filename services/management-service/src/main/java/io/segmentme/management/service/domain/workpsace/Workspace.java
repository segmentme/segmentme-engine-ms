package io.segmentme.management.service.domain.workpsace;

import io.segmentme.core.db.config.mongo.CascadeSave;
import io.segmentme.core.domain.DbObject;
import io.segmentme.models.shared.analysis.IntegrationPoint;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "workspace")
public class Workspace extends DbObject {
    private String name;

    private List<IntegrationPoint> integrationPoints;

    private WorkspaceConfiguration configuration;

    @DBRef
    @CascadeSave
    private List<UserProfile> userProfiles;

    private boolean isDefault;

}

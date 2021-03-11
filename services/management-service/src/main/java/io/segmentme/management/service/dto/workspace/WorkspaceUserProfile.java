package io.segmentme.management.service.dto.workspace;

import io.segmentme.core.domain.workpsace.Role;
import lombok.Data;

@Data
public class WorkspaceUserProfile {
    private Role role;

    private String name;

    private String email;

    private String profileId;

    private String userId;

}

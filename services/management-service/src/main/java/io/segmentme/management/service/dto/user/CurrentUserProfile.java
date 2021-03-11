package io.segmentme.management.service.dto.user;

import io.segmentme.core.domain.workpsace.Role;
import lombok.Data;

@Data
public class CurrentUserProfile {
    private String workspaceId;

    private String workspaceName;

    private Role role;

    private boolean active;

    private boolean isDefault;
}

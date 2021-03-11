package io.segmentme.management.service.dto;

import io.segmentme.core.domain.workpsace.Role;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserProfileHolder {
    private String userId;

    private String workspaceId;

    private Role role;
}

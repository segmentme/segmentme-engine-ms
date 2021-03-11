package io.segmentme.management.service.security;

import io.segmentme.core.domain.DbObject;
import io.segmentme.core.domain.workpsace.UserProfile;
import io.segmentme.management.service.service.user.UserService;
import io.segmentme.management.service.workspace.UserProfileManager;
import io.segmentme.web.configuration.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkspaceSecurityService {

    private final UserProfileManager userProfileManager;
    private final UserService userService;

    public boolean isWorkspaceMember(String id) {
        return userProfileManager.getUserProfiles(userService.findByExternalId(SecurityUtils.currentUserId()).map(DbObject::getId).orElse(null))
            .stream()
            .map(UserProfile::getWorkspaceId)
            .map(it -> it.equalsIgnoreCase(id))
            .findFirst()
            .isPresent();
    }
}

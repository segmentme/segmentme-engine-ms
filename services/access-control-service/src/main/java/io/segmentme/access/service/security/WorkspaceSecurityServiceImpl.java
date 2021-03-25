package io.segmentme.access.service.security;

import io.segmentme.core.domain.workpsace.UserProfile;
import io.segmentme.helpers.dao.service.UserProfileService;
import io.segmentme.security.WorkspaceSecurityService;
import io.segmentme.web.configuration.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkspaceSecurityServiceImpl implements WorkspaceSecurityService {

    private final UserProfileService userProfileService;

    @Override
    public boolean isWorkspaceMember(String workspaceId) {
        return userProfileService.getUserProfiles(SecurityUtils.currentUserId())
            .stream()
            .map(UserProfile::getWorkspaceId)
            .map(it -> it.equalsIgnoreCase(workspaceId))
            .findFirst()
            .isPresent();
    }
}

package io.segmentme.management.service.workspace;

import io.segmentme.core.domain.workpsace.UserProfile;
import io.segmentme.helpers.dao.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfileManager {
    private final UserProfileService userProfileService;

    public List<UserProfile> getUserProfiles(String userId) {
        return userProfileService.getUserProfiles(userId);
    }

    public List<UserProfile> getWorkspaceProfiles(String workspaceId) {
        return userProfileService.getWorkspaceProfiles(workspaceId);
    }

}

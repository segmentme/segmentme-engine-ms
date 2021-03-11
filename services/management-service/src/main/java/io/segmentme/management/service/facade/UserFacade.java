package io.segmentme.management.service.facade;

import io.segmentme.core.domain.workpsace.UserProfile;
import io.segmentme.management.service.dto.user.CurrentUserProfile;
import io.segmentme.management.service.dto.user.UserBasicInfo;
import io.segmentme.management.service.dto.user.UserDetails;
import io.segmentme.management.service.dto.user.UserHolder;
import io.segmentme.management.service.user.UserManager;
import io.segmentme.management.service.workspace.UserProfileManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final UserManager userManager;

    private final UserProfileManager userProfileManager;

    public UserDetails getUserDetails(String externalId) {
        return getUserDetails(userManager.getByExternalId(externalId));
    }

    private UserDetails getUserDetails(UserHolder createdUser) {
        List<UserProfile> userProfiles = userProfileManager.getUserProfiles(createdUser.getId());
        List<CurrentUserProfile> workspaceProfiles = userProfiles.stream()
            .map(this::convertToWorkspaceProfile)
            .map(it -> it.setActive(it.getWorkspaceId().equals(createdUser.getLastActiveWorkspace()))).collect(Collectors.toList());

        if (workspaceProfiles.stream().noneMatch(CurrentUserProfile::isActive)) {
            workspaceProfiles.stream().filter(CurrentUserProfile::isDefault).findFirst().ifPresent(it -> {
                it.setActive(true);
                userManager.switchWorkspace(createdUser.getId(), it.getWorkspaceId());
            });
        }
        return new UserDetails()
            .setUserBasicInfo(convertToBasicUserInfo(createdUser))
            .setProfiles(workspaceProfiles);
    }

    private UserBasicInfo convertToBasicUserInfo(UserHolder createdUser) {
        return new UserBasicInfo().setEmail(createdUser.getEmail()).setId(createdUser.getId()).setName(createdUser.getName());
    }

    private CurrentUserProfile convertToWorkspaceProfile(UserProfile userProfile) {
        return new CurrentUserProfile().setRole(userProfile.getRole())
            .setDefault(userProfile.isDefault())
            .setWorkspaceId(userProfile.getWorkspaceId())
            .setWorkspaceName(userProfile.getWorkspaceName());
    }

    public void switchWorkspace(String userId, String workspaceId) {
        userManager.switchWorkspace(userId, workspaceId);
    }

    public void acknowledgeUser(String id, String email, String fullName) {
        userManager.acknowledgeUser(new UserHolder().setName(fullName).setEmail(email).setExternalId(id));
    }
}

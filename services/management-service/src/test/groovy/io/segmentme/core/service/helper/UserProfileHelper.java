package io.segmentme.core.service.helper;

import io.segmentme.management.service.domain.user.User;
import io.segmentme.management.service.domain.workpsace.Role;
import io.segmentme.management.service.domain.workpsace.UserProfile;
import io.segmentme.management.service.domain.workpsace.Workspace;
import io.segmentme.management.service.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class UserProfileHelper {

    private final UserProfileRepository userProfileRepository;

    public static UserProfile createProfile(User user, Workspace workspace) {
        return new UserProfile().setRole(Role.OWNER).setUserId(user.getId());
    }

    public UserProfile createAndSaveProfile(User user, Workspace workspace) {
        return userProfileRepository.save(createProfile(user, workspace));
    }
}

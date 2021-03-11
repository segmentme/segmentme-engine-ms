package io.segmentme.core.db.helper;

import io.segmentme.core.domain.user.User;
import io.segmentme.core.domain.workpsace.Role;
import io.segmentme.core.domain.workpsace.UserProfile;
import io.segmentme.core.domain.workpsace.Workspace;
import io.segmentme.core.service.repository.UserProfileRepository;
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

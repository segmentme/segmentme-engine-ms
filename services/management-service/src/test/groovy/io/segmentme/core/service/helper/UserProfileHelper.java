package io.segmentme.core.service.helper;

import io.segmentme.core.domain.workpsace.*;
import io.segmentme.helpers.dao.repository.UserProfileRepository;
import io.segmentme.management.domain.user.User;
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

package io.segmentme.core.service.helper;

import io.segmentme.management.service.domain.user.User;
import io.segmentme.management.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public
class UserHelper {

    private final UserRepository userRepository;

    public static User createUser() {
        return new User().setEmail(UUID.randomUUID().toString() + "@aa.com").setName("name").setExternalId(UUID.randomUUID().toString());
    }

    public User createAndSaveUser() {
        return userRepository.save(createUser());
    }
}

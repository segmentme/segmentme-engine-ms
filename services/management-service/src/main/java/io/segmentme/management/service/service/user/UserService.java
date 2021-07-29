package io.segmentme.management.service.service.user;

import io.segmentme.core.db.service.AbstractDatabaseService;
import io.segmentme.management.service.domain.user.User;
import io.segmentme.management.service.repository.UserRepository;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Service;

import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
@Service
public class UserService extends AbstractDatabaseService<User, UserRepository> {

    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public Optional<User> findByExternalId(String externalId) {
        return repository.findByExternalId(externalId);
    }
}

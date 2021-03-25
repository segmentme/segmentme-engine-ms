package io.segmentme.access.service.repository;

import io.segmentme.management.domain.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByExternalId(String id);
}

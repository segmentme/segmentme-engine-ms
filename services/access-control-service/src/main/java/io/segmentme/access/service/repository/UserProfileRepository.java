package io.segmentme.access.service.repository;

import io.segmentme.access.service.domain.workpsace.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserProfileRepository extends MongoRepository<UserProfile, String> {

    List<UserProfile> findAllByUserId(String id);

}

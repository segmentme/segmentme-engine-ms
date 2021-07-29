package io.segmentme.management.service.repository;

import io.segmentme.management.service.domain.workpsace.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserProfileRepository extends MongoRepository<UserProfile, String> {

    List<UserProfile> findAllByUserId(String id);

    List<UserProfile> getAllByWorkspaceId(String workspaceId);
}

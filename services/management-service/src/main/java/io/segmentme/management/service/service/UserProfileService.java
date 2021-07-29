package io.segmentme.management.service.service;

import io.segmentme.core.db.service.AbstractDatabaseService;
import io.segmentme.management.service.domain.workpsace.UserProfile;
import io.segmentme.management.service.repository.UserProfileRepository;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Service
@Data
@RequiredArgsConstructor
public class UserProfileService extends AbstractDatabaseService<UserProfile, UserProfileRepository> {

    public List<UserProfile> getUserProfiles(String id) {
        return repository.findAllByUserId(id);
    }

    public List<UserProfile> getWorkspaceProfiles(String workspaceId) {
        return repository.getAllByWorkspaceId(workspaceId);
    }
}

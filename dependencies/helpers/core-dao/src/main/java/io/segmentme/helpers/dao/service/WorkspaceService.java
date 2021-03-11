package io.segmentme.helpers.dao.service;

import io.segmentme.core.db.service.AbstractDatabaseService;
import io.segmentme.core.domain.workpsace.UserProfile;
import io.segmentme.core.domain.workpsace.Workspace;
import io.segmentme.helpers.dao.repository.WorkspaceRepository;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Service
@Data
@RequiredArgsConstructor
public class WorkspaceService extends AbstractDatabaseService<Workspace, WorkspaceRepository> {

    private final UserProfileService userProfileService;

    public Optional<Workspace> findByIntegrationPointKey(String integrationPointKey) {
        return repository.findByIntegrationPointsKey(integrationPointKey);
    }

    public List<Workspace> findAllUserWorkspaces(String userId) {
        return IteratorUtils.toList(repository.findAllById(userProfileService.getUserProfiles(userId).stream().map(UserProfile::getWorkspaceId).collect(Collectors.toList())).iterator());
    }
}

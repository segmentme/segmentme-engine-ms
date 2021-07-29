package io.segmentme.access.service.service;

import io.segmentme.access.service.domain.workpsace.Workspace;
import io.segmentme.access.service.repository.WorkspaceRepository;
import io.segmentme.core.db.service.AbstractDatabaseService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
@Service
@Data
@RequiredArgsConstructor
public class WorkspaceService extends AbstractDatabaseService<Workspace, WorkspaceRepository> {

    private final UserProfileService userProfileService;

    public Optional<Workspace> findByIntegrationPointKey(String integrationPointKey) {
        return repository.findByIntegrationPointsKey(integrationPointKey);
    }
}

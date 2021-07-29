package io.segmentme.management.service.service;

import io.segmentme.core.db.service.AbstractDatabaseService;
import io.segmentme.management.service.domain.workpsace.Workspace;
import io.segmentme.management.service.repository.WorkspaceRepository;
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

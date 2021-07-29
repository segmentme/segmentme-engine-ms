package io.segmentme.analysis.service;

import io.segmentme.analysis.domain.workpsace.Workspace;
import io.segmentme.analysis.repository.WorkspaceRepository;
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


    public Optional<Workspace> findByIntegrationPointKey(String integrationPointKey) {
        return repository.findByIntegrationPointsKey(integrationPointKey);
    }

}

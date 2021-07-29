package io.segmentme.access.service.repository;

import io.segmentme.access.service.domain.workpsace.Workspace;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface WorkspaceRepository extends MongoRepository<Workspace, String> {

    Optional<Workspace> findByIntegrationPointsKey(String integrationPointKey);

}

package io.segmentme.analysis.repository;

import io.segmentme.analysis.domain.workpsace.Workspace;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface WorkspaceRepository extends MongoRepository<Workspace, String> {

    Optional<Workspace> findByIntegrationPointsKey(String integrationPointKey);

}

package io.segmentme.measurement.service.repository;

import io.segmentme.measurement.service.domain.workpsace.Workspace;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface WorkspaceRepository extends MongoRepository<Workspace, String> {

    Optional<Workspace> findByIntegrationPointsKey(String integrationPointKey);

}

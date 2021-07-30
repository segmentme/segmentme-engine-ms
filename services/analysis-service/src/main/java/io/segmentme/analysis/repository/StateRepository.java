package io.segmentme.analysis.repository;

import io.segmentme.analysis.domain.state.State;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface StateRepository extends MongoRepository<State, String> {

    List<State> findByIntegrationPointKey(String integrationPoint);

}

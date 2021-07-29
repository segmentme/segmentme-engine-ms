package io.segmentme.access.service.repository;

import io.segmentme.access.service.domain.state.State;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StateRepository extends MongoRepository<State, String> {

}

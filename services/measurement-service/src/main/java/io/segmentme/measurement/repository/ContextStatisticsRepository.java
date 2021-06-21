package io.segmentme.measurement.repository;

import io.segmentme.measurement.domain.ContextStatistic;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContextStatisticsRepository extends MongoRepository<ContextStatistic, String> {


    ContextStatistic findByContextId(String contextId);

}

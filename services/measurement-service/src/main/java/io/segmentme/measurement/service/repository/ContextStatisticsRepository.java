package io.segmentme.measurement.service.repository;

import io.segmentme.measurement.service.domain.statistic.ContextStatistic;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContextStatisticsRepository extends MongoRepository<ContextStatistic, String> {


    ContextStatistic findByContextId(String contextId);

}

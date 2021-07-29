package io.segmentme.access.service.repository;

import io.segmentme.access.service.domain.statistic.ContextStatistic;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContextStatisticsRepository extends MongoRepository<ContextStatistic, String> {


    ContextStatistic findByContextId(String contextId);

}

package io.segmentme.access.service.repository;

import io.segmentme.access.service.domain.statistic.StatisticLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StatisticRepository extends MongoRepository<StatisticLog, String> {
}

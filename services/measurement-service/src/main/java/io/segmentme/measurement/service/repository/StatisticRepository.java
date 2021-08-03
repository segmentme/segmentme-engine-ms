package io.segmentme.measurement.service.repository;

import io.segmentme.measurement.service.domain.statistic.StatisticLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StatisticRepository extends MongoRepository<StatisticLog, String> {
}

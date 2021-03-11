package io.segmentme.measurement.repository;

import io.segmentme.analysis.domain.statistic.StatisticLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StatisticRepository extends MongoRepository<StatisticLog, String> {
}

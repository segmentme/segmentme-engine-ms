package io.segmentme.measurement.repository;

import io.segmentme.analysis.domain.statistic.AnalyzedData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AnalyzedDataRepository extends MongoRepository<AnalyzedData, String> {

    AnalyzedData findByHash(String hash);


}

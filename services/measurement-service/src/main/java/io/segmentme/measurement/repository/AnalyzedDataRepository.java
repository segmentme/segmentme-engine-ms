package io.segmentme.measurement.repository;

import io.segmentme.analysis.domain.statistic.AnalyzedData;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AnalyzedDataRepository extends MongoRepository<AnalyzedData, String> {

    AnalyzedData findByHash(String hash);

    List<AnalyzedData> findByHashIn(List<String> hash);

}

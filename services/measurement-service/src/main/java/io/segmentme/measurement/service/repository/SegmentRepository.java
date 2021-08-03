package io.segmentme.measurement.service.repository;

import io.segmentme.measurement.service.domain.segment.Segment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SegmentRepository extends MongoRepository<Segment, String> {

    List<Segment> findByIntegrationPointKey(String integrationPointKey);
}

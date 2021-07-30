package io.segmentme.analysis.repository;

import io.segmentme.analysis.domain.segment.Segment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SegmentRepository extends MongoRepository<Segment, String> {

    List<Segment> findByIntegrationPointKeyAndActive(String integrationPointKey, boolean isActive);

}

package io.segmentme.management.service.repository;

import io.segmentme.management.service.domain.segment.Segment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SegmentRepository extends MongoRepository<Segment, String> {

    List<Segment> findByIntegrationPointKey(String integrationPointKey);

    List<Segment> findByIntegrationPointKeyIn(Iterable<String> integrationPointKeys);

    List<Segment> findByContextId(String contextId);

    Segment findByIntegrationPointKeyAndName(String integrationPointKey, String name);

    List<Segment> findByOpenedForPercentageLessThan(int percentage);
}

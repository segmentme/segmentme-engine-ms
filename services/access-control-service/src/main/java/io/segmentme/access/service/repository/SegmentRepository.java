package io.segmentme.access.service.repository;

import io.segmentme.access.service.domain.segment.Segment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SegmentRepository extends MongoRepository<Segment, String> {
}

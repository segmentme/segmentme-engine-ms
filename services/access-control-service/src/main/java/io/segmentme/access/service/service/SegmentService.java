package io.segmentme.access.service.service;

import io.segmentme.access.service.domain.segment.Segment;
import io.segmentme.access.service.repository.SegmentRepository;
import io.segmentme.core.db.service.AbstractDatabaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SegmentService extends AbstractDatabaseService<Segment, SegmentRepository> {

}

package io.segmentme.access.service.service;

import io.segmentme.access.service.domain.segment.Segment;
import io.segmentme.access.service.repository.SegmentRepository;
import io.segmentme.core.db.service.AbstractDatabaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class SegmentService extends AbstractDatabaseService<Segment, SegmentRepository> {


    public List<Segment> findByIntegrationPointKey(String integrationPointKey) {
        return repository.findByIntegrationPointKey(integrationPointKey);
    }


    public List<Segment> findByIds(Iterable<String> ids) {
        return StreamSupport.stream(repository.findAllById(ids).spliterator(), false)
            .collect(Collectors.toList());
    }


}

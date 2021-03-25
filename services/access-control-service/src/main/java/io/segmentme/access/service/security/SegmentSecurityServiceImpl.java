package io.segmentme.access.service.security;

import io.segmentme.core.domain.segment.Segment;
import io.segmentme.helpers.dao.service.SegmentService;
import io.segmentme.security.SegmentSecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SegmentSecurityServiceImpl implements SegmentSecurityService {

    private final SegmentService segmentService;

    private final SecurityService securityService;

    @Override
    public boolean isManaged(String segmentId, String userId) {
        return securityService.isValidIntegrationPointKey(segmentService
            .findById(segmentId).map(Segment::getIntegrationPointKey).orElse(null), userId);
    }
}

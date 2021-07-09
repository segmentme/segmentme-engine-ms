package io.segmentme.management.service.service.management;

import io.segmentme.core.domain.segment.Segment;
import io.segmentme.helpers.dao.repository.SegmentRepository;
import io.segmentme.management.service.service.clients.MeasurementClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PercentageDistributorService {

    private final SegmentRepository segmentRepository;

    private final MeasurementClient measurmentClient;


    @Scheduled(cron = "0 0/5 * * * ?")
    public void redistributePercentage() {
        List<Segment> segments = segmentRepository.findByOpenedForPercentageLessThan(100);
        log.info("Redistribute percentage for {} segments", segments.size());
        segments.forEach(measurmentClient::redistributePercentage);
    }
}

package io.segmentme.management.service.service.management;

import io.segmentme.core.domain.segment.Segment;
import io.segmentme.helpers.dao.repository.SegmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PercentageDistributorService {

    private final SegmentRepository segmentRepository;

    private final String REDISTRIBUTE_PERCENTAGE_PATH = "/measurement/redistribute/{contextId}";
    private final String SEGMENT_ID_QPARAM = "segmentId";
    private final String PERCENTAGE_QPARAM = "/measurement/redistribute";


    @Qualifier("measurement-service")
    private final WebClient measurementServiceWebClient;


    @Scheduled(cron = "0 0/5 * * * ?")
    public void redistributePercentage() {
        List<Segment> segments = segmentRepository.findByOpenedForPercentageLessThan(100);
        log.info("Redistribute percentage for {} segments", segments.size());
        segments.forEach(segment -> {
            Optional.ofNullable(measurementServiceWebClient.put()
                .uri(builder -> builder.path(REDISTRIBUTE_PERCENTAGE_PATH)
                    .queryParam(PERCENTAGE_QPARAM, segment.getOpenedForPercentage())
                    .queryParam(SEGMENT_ID_QPARAM, segment.getId())
                    .build(segment.getContextId()))
                .exchangeToMono(result -> result.statusCode().isError() ? result.createException().flatMap(Mono::error) : result.bodyToMono(Void.class))
                .block());

        });


    }
}

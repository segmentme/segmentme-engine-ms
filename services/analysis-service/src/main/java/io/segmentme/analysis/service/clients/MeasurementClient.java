package io.segmentme.analysis.service.clients;

import io.segmentme.statistics.dto.ParticipantStatisticDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Data
@Slf4j
@Component
@RequiredArgsConstructor
public class MeasurementClient {

    private final static String PARTICIPANT_PATH = "/participant/{contextId}";
    private final static String PARTICIPANT_PATH_UNIQUENESS_VALUE = "uniquenessValue";

    @Qualifier("measurement-service")
    private final WebClient measurementServiceWebClient;

    public Optional<ParticipantStatisticDto> findParticipantStatistic(String contextId, Object uniquenessValue) {
        return measurementServiceWebClient
                .get()
                .uri(builder -> builder.path(PARTICIPANT_PATH).queryParam(PARTICIPANT_PATH_UNIQUENESS_VALUE, uniquenessValue).build(contextId))
                .exchangeToMono(result -> result.statusCode().isError() ? result.createException().flatMap(Mono::error) : result.bodyToMono(ParticipantStatisticDto.class))
                .blockOptional();
    }
}


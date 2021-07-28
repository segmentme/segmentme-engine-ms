package io.segmentme.management.service.service.clients;

import io.segmentme.core.domain.segment.Segment;
import io.segmentme.management.service.service.clients.dto.ParticipantAcknowledgeRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Data
@Component
@RequiredArgsConstructor
public class MeasurementClient {

    public static final String PARTICIPANT_PATH = "/participant";
    private final String REDISTRIBUTE_PERCENTAGE_PATH = "/measurement/redistribute/{contextId}";
    private final String REFRESH_PARTICIPANTS = "/measurement/refresh-participants/{contextId}";
    private final String SEGMENT_ID_QPARAM = "segmentId";
    private final String PERCENTAGE_QPARAM = "percentage";
    private final String UNIQUENESSID_QPARAM = "uniquenessIdentifier";


    @Qualifier("measurement-service")
    private final WebClient measurementServiceWebClient;

    public void redistributePercentage(Segment segment) {
        measurementServiceWebClient.put()
            .uri(builder -> builder.path(REDISTRIBUTE_PERCENTAGE_PATH)
                .queryParam(PERCENTAGE_QPARAM, segment.getOpenedForPercentage())
                .queryParam(SEGMENT_ID_QPARAM, segment.getId())
                .build(segment.getContextId()))
            .exchangeToMono(result -> result.statusCode().isError() ? result.createException().flatMap(Mono::error) : result.bodyToMono(Void.class))
            .block();

    }

    public void refreshContextParticipants(String contextId, String uniquenessId) {
        measurementServiceWebClient.put()
            .uri(builder -> builder.path(REFRESH_PARTICIPANTS)
                .queryParam(UNIQUENESSID_QPARAM, uniquenessId)
                .build(contextId))
            .exchangeToMono(result -> result.statusCode().isError() ? result.createException().flatMap(Mono::error) : result.bodyToMono(Void.class))
            .block();
    }


    public void acknowledgeContextParticipant(ParticipantAcknowledgeRequest acknowledgeRequest) {
        measurementServiceWebClient.post()
            .uri(PARTICIPANT_PATH)
            .bodyValue(acknowledgeRequest)
            .retrieve()
            .toBodilessEntity()
            .block();
    }
}


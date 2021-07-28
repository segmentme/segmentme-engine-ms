package io.segmentme.measurement.resource;

import io.segmentme.measurement.converter.ParticipantStatisticConverter;
import io.segmentme.measurement.domain.ParticipantStatistic;
import io.segmentme.measurement.dto.ParticipantAcknowledgeRequest;
import io.segmentme.measurement.dto.statistic.ParticipantStatisticDto;
import io.segmentme.measurement.service.ParticipantStatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/participant")
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantStatisticService participantStatisticService;

    @PostMapping
    public void acknowledgeParticipant(@RequestBody ParticipantAcknowledgeRequest contextDataHolder) {
        participantStatisticService.acknowledgeParticipant(contextDataHolder);
    }

    @GetMapping("/{contextId}")
    public Mono<ParticipantStatisticDto> findParticipantStatistic(@PathVariable String contextId,
                                                                  @RequestParam Object uniquenessValue) {
        ParticipantStatistic participant = participantStatisticService.getParticipant(contextId, uniquenessValue);
        return Mono.just(ParticipantStatisticConverter.toDto(participant));
    }
}

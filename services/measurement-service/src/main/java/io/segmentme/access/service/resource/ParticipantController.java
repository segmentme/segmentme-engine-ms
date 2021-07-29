package io.segmentme.access.service.resource;

import io.segmentme.access.service.converter.ParticipantStatisticConverter;
import io.segmentme.access.service.domain.statistic.ParticipantStatistic;
import io.segmentme.access.service.dto.ParticipantAcknowledgeRequest;
import io.segmentme.access.service.dto.statistic.ParticipantStatisticDto;
import io.segmentme.access.service.service.ParticipantStatisticService;
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

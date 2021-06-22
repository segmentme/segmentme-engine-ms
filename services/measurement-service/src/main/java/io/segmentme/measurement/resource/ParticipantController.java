package io.segmentme.measurement.resource;

import io.segmentme.analysis.dto.CollectedAnalysysStatisticDto;
import io.segmentme.measurement.converter.ParticipantStatisticConverter;
import io.segmentme.measurement.domain.ParticipantStatistic;
import io.segmentme.measurement.service.ParticipantStatisticService;
import io.segmentme.statistics.dto.ParticipantStatisticDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/participant")
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantStatisticService participantStatisticService;

    @PostMapping
    public void acknowledgeParticipant(@RequestBody CollectedAnalysysStatisticDto.ContextDataHolder contextDataHolder) {
        participantStatisticService.acknowledgeParticipant(contextDataHolder);
    }

    @GetMapping("/{contextId}")
    public ParticipantStatisticDto findParticipantStatistic(@PathVariable String contextId,
                                                            @RequestParam Object uniquenessValue) {
        ParticipantStatistic participant = participantStatisticService.getParticipant(contextId, uniquenessValue);
        return ParticipantStatisticConverter.toDto(participant);
    }
}

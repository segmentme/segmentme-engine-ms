package io.segmentme.measurement.resource;

import io.segmentme.analysis.dto.CollectedAnalysysStatisticDto;
import io.segmentme.measurement.service.ParticipantStatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/participant")
@RequiredArgsConstructor
public class ParticipantController {
    private final ParticipantStatisticService participantStatisticService;


    @PostMapping
    public void acknowledgeParticipant(@RequestBody CollectedAnalysysStatisticDto.ContextDataHolder contextDataHolder) {
        participantStatisticService.acknowledgeParticipant(contextDataHolder);
    }


}

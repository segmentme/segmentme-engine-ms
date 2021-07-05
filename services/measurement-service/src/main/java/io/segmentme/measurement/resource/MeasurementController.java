package io.segmentme.measurement.resource;

import io.segmentme.measurement.service.StatisticManager;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/measurement")
@RequiredArgsConstructor
public class MeasurementController {

    private final StatisticManager statisticManager;

    @PutMapping("/redistribute/{contextId}")
    public void findParticipantStatistic(@PathVariable String contextId,
                                         @RequestParam String segmentId, @RequestParam Integer percentage) {
        statisticManager.redistributePercentage(contextId, segmentId, percentage);
    }
}

package io.segmentme.measurement.resource;

import io.segmentme.measurement.service.MeasurementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/measurement")
@RequiredArgsConstructor
public class MeasurementController {

    private final MeasurementService measurementService;

    @PutMapping("/redistribute/{contextId}")
    public void redistribute(@PathVariable String contextId,
                             @RequestParam String segmentId, @RequestParam Integer percentage) {
        measurementService.redistributePercentage(contextId, segmentId, percentage);
    }

    @PutMapping("/refresh-participants/{contextId}")
    public void refreshParticipants(@PathVariable String contextId, @RequestParam String uniquenessIdentifier) {
        measurementService.refreshParticipants(contextId, uniquenessIdentifier);
    }
}

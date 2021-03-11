package io.segmentme.analysis.resource;

import io.segmentme.analysis.dto.AnalysisData;
import io.segmentme.analysis.dto.AnalysisRequest;
import io.segmentme.analysis.dto.AnalysisResult;
import io.segmentme.analysis.dto.DebugRequest;
import io.segmentme.analysis.service.converter.SegmentConverter;
import io.segmentme.analysis.service.segment.AnalysisService;
import io.segmentme.analysis.service.state.StateAnalysisResult;
import io.segmentme.analysis.service.state.StateAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/analysis")
public class AnalysisController {

    private final AnalysisService analysisService;

    private final StateAnalysisService stateAnalysisService;


    @PostMapping("/debug")
    public AnalysisResult analyze(@RequestBody @Valid DebugRequest request) {
        return analysisService.debug(request.getIntegrationPointKey(), request.getContextId(), request.getPayload(), SegmentConverter.of(request.getSegment(), null, request.getIntegrationPointKey()));
    }


    @PostMapping("/analyze")
    public AnalysisResult analyze(@RequestBody @Valid AnalysisRequest request) {
        return AnalysisResult.of(analysisService.analyze(request.getIntegrationPointKey(), request.getContextId(), request.getAnalysisData()), null);
    }


    @PostMapping("/state/analyze/{integrationPointKey}")
    public List<StateAnalysisResult> analyze(@PathVariable String integrationPointKey, @RequestBody AnalysisData analysisData) {
        return stateAnalysisService.analyse(integrationPointKey, analysisData);
    }
}

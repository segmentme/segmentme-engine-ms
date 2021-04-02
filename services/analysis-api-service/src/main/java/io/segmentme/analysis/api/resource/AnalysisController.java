package io.segmentme.analysis.api.resource;

import io.segmentme.analysis.api.dto.SdkAnalysisRequest;
import io.segmentme.analysis.api.dto.SdkAnalysisResponse;
import io.segmentme.analysis.api.service.AnalysisService;
import io.segmentme.analysis.dto.AnalysisResult;
import io.segmentme.analysis.dto.DebugRequest;
import io.segmentme.models.shared.analysis.IntegrationPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/analysis")
public class AnalysisController {

private final AnalysisService analysisService;

    @PostMapping("/debug")
    public AnalysisResult analyze(@RequestParam String contextId,
                               @RequestParam String integrationPointKey,
                               @RequestBody @Valid DebugRequest request) {
        request.setContextId(contextId).setIntegrationPointKey(integrationPointKey);
        return analysisService.debug(integrationPointKey, request);
    }

    @GetMapping("/connect")
    public IntegrationPoint connect(@RequestHeader("integration-point-key") String integrationPointKey) {
//        return sdkFacade.connect(integrationPointKey);
        return null;
    }


    @PostMapping("/analysis/analyze")
    public SdkAnalysisResponse analyze(@RequestHeader("integration-point-key") String integrationPointKey,
                                       @RequestBody SdkAnalysisRequest sdkAnalysisRequest) {
        return analysisService.analyze(integrationPointKey, sdkAnalysisRequest);
    }

//    @PostMapping("/state/analyze/{integrationPointKey}")
//    public List<StateAnalysisResult> analyze(@PathVariable String integrationPointKey, @RequestBody AnalysisData analysisData) {
//        return stateAnalysisService.analyse(integrationPointKey, analysisData);
//    }
//
//
//    @PostMapping("/state/analyze/{integrationPointKey}")
//    public List<StateAnalysisResult> analyze(@PathVariable String integrationPointKey, @RequestBody AnalysisData analysisData) {
//        return stateAnalysisService.analyse(integrationPointKey, analysisData);
//    }
}

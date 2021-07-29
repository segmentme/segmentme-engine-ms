package io.segmentme.analysis.service.state;

import io.segmentme.analysis.domain.state.State;
import io.segmentme.analysis.dto.AnalysisData;
import io.segmentme.analysis.dto.SegmentAnalysisResult;
import io.segmentme.analysis.service.StateService;
import io.segmentme.analysis.service.segment.AnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StateAnalysisService {

    private final StateService stateService;

    private final AnalysisService analysisService;

    public List<StateAnalysisResult> analyse(String integrationPointKey, AnalysisData analysisData) {
        List<State> sates = stateService.findByIntegrationPointKey(integrationPointKey);
        return sates.stream()
            .map(it -> StateAnalysisResult.of(it.getName(), isMatched(it, analysisData) ? it.getValue() : it.getDefaultValue()))
            .collect(Collectors.toList());
    }

    private boolean isMatched(State state, AnalysisData analysisData) {
        return analysisService.analyze(state.getIntegrationPointKey(), analysisData, state.getSegment())
            .stream()
            .findFirst()
            .map(SegmentAnalysisResult::isValue)
            .orElse(false);
    }
}

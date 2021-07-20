package io.segmentme.analysis.api.clients;

import io.segmentme.analysis.api.dto.SdkAnalysisRequest;
import io.segmentme.analysis.api.dto.SdkAnalysisResponse;
import io.segmentme.analysis.dto.AnalysisRequest;
import io.segmentme.analysis.dto.AnalysisResult;
import io.segmentme.analysis.dto.DebugRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Data
@Component
@RequiredArgsConstructor
public class AnalysisClient {
    private static final String ANALYZE_PATH = "/analysis/analyze";
    private static final String DEBUG_PATH = "/analysis/debug";
    @Qualifier("analysis-service-client")
    private final WebClient analysisServiceWebClient;

    public AnalysisResult analyse(String integrationPointKey, SdkAnalysisRequest sdkAnalysisRequest, SdkAnalysisResponse response) {
        return analysisServiceWebClient.post()
            .uri(ANALYZE_PATH)
            .bodyValue(new AnalysisRequest().setContextId(response.getContextId()).setIntegrationPointKey(integrationPointKey).setAnalysisData(sdkAnalysisRequest.getAnalysisData()))
            .retrieve().bodyToMono(AnalysisResult.class).block();
    }


    public AnalysisResult debug( DebugRequest request) {
        return analysisServiceWebClient.post()
            .uri(DEBUG_PATH)
            .bodyValue(request)
            .retrieve().bodyToMono(AnalysisResult.class).block();


    }
}

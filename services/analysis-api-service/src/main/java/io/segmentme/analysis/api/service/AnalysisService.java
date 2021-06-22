package io.segmentme.analysis.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.analysis.api.dto.ContextSchemaRefreshRequest;
import io.segmentme.analysis.api.dto.ContextSchemaShortInfo;
import io.segmentme.analysis.api.dto.SdkAnalysisRequest;
import io.segmentme.analysis.api.dto.SdkAnalysisResponse;
import io.segmentme.analysis.dto.AnalysisRequest;
import io.segmentme.analysis.dto.AnalysisResult;
import io.segmentme.analysis.dto.DebugRequest;
import io.segmentme.models.shared.analysis.IntegrationPoint;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    @Qualifier("analysis-service-client")
    private final WebClient analysisServiceWebClient;

    @Qualifier("management-service-client")
    private final WebClient managementServiceClient;

    public SdkAnalysisResponse analyze(String integrationPointKey, SdkAnalysisRequest sdkAnalysisRequest) {
        SdkAnalysisResponse response = new SdkAnalysisResponse();
        if (StringUtils.isEmpty(sdkAnalysisRequest.getContextId())) {
            response.setContextId(actualizeSchema(sdkAnalysisRequest.getContextKey(), integrationPointKey, sdkAnalysisRequest.getAnalysisData().getPayload()));
        }

        AnalysisResult block = analysisServiceWebClient.post()
            .uri("/analysis/analyze")
            .bodyValue(new AnalysisRequest().setContextId(response.getContextId()).setIntegrationPointKey(integrationPointKey).setAnalysisData(sdkAnalysisRequest.getAnalysisData()))
            .retrieve().bodyToMono(AnalysisResult.class).block();

        return response.setAnalyzedSegments(block.getSegmentAnalysisResults());
    }

    public IntegrationPoint getIntegrationPointKey(String integrationPointKey) {
        return managementServiceClient.get()
            .uri("/workspace/integration-point?integrationPointKey=" + integrationPointKey)
            .retrieve()
            .bodyToMono(IntegrationPoint.class)
            .block();
    }

    private String actualizeSchema(String contextKey, String integrationPointKey, JsonNode jsonNode) {
        ContextSchemaShortInfo block = managementServiceClient.put()
            .uri("/context-schema/actualize")
            .bodyValue(new ContextSchemaRefreshRequest()
                .setIntegrationPointId(integrationPointKey)
                .setContextKey(contextKey)
                .setPayload(jsonNode)
            )
            .retrieve().bodyToMono(ContextSchemaShortInfo.class).block();
        return block.getId();
    }

    public AnalysisResult debug(String integrationPointKey, DebugRequest request) {
        return analysisServiceWebClient.post()
            .uri("/analysis/debug")
            .bodyValue(request)
            .retrieve().bodyToMono(AnalysisResult.class).block();


    }
}

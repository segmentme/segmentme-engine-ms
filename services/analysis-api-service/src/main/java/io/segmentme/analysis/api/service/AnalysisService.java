package io.segmentme.analysis.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.analysis.api.dto.ContextSchemaRefreshRequest;
import io.segmentme.analysis.api.dto.ContextSchemaShortInfo;
import io.segmentme.analysis.api.dto.SdkAnalysisRequest;
import io.segmentme.analysis.api.dto.SdkAnalysisResponse;
import io.segmentme.analysis.dto.AnalysisRequest;
import io.segmentme.analysis.dto.AnalysisResult;
import io.segmentme.analysis.dto.DebugRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service

public class AnalysisService {
    WebClient analysisServiceWebClient = WebClient.builder().baseUrl("http://localhost:8081").build();
    WebClient managementService = WebClient.builder().baseUrl("http://localhost:8082").build();


    public SdkAnalysisResponse analyze(String integrationPointKey, SdkAnalysisRequest sdkAnalysisRequest) {
        SdkAnalysisResponse response = new SdkAnalysisResponse();
        if (StringUtils.isEmpty(sdkAnalysisRequest.getContextId())) {
            response.setContextId(actualizeSchema(sdkAnalysisRequest.getContextKey(), sdkAnalysisRequest.getAnalysisData().getPayload()));
        }

        AnalysisResult block = analysisServiceWebClient.post()
            .uri("/analysis/analyze")
            .bodyValue(new AnalysisRequest().setContextId(response.getContextId()).setIntegrationPointKey(integrationPointKey).setAnalysisData(sdkAnalysisRequest.getAnalysisData()))
            .retrieve().bodyToMono(AnalysisResult.class).block();

        return response.setAnalyzedSegments(block.getSegmentAnalysisResults());
    }

    private String actualizeSchema(String contextKey, JsonNode jsonNode) {
        ContextSchemaShortInfo block = analysisServiceWebClient.post()
            .uri("/analysis/analyze")
            .bodyValue(new ContextSchemaRefreshRequest().setContextKey(contextKey).setPayload(jsonNode))
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

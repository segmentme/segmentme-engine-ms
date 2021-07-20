package io.segmentme.analysis.api.service;

import io.segmentme.analysis.api.clients.AnalysisClient;
import io.segmentme.analysis.api.clients.ManagementClient;
import io.segmentme.analysis.api.dto.SdkAnalysisRequest;
import io.segmentme.analysis.api.dto.SdkAnalysisResponse;
import io.segmentme.analysis.dto.AnalysisResult;
import io.segmentme.analysis.dto.DebugRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalysisService {
    private final ManagementClient managementClient;

    private final AnalysisClient analysisClient;


    public SdkAnalysisResponse analyze(String integrationPointKey, SdkAnalysisRequest sdkAnalysisRequest) {
        SdkAnalysisResponse response = new SdkAnalysisResponse();
        if (StringUtils.isEmpty(sdkAnalysisRequest.getContextId())) {
            response.setContextId(managementClient.actualizeSchema(sdkAnalysisRequest.getContextKey(), integrationPointKey, sdkAnalysisRequest.getAnalysisData().getPayload()));
        } else {
            response.setContextId(sdkAnalysisRequest.getContextId());
        }

        AnalysisResult block = analysisClient.analyse(integrationPointKey, sdkAnalysisRequest, response);

        return response.setAnalyzedSegments(block.getSegmentAnalysisResults());
    }


    public AnalysisResult debug(DebugRequest request) {
        return analysisClient.debug(request);
    }

}

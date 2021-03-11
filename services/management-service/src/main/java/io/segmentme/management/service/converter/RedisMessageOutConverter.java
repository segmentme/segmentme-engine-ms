package io.segmentme.management.service.converter;

import analysis.SdkAnalysisResponse;
import analysis.SegmentAnalysisResult;
import io.segmentme.redis.dto.AnalysisResponse;
import io.segmentme.redis.dto.SegmentAnalysisResutl;
import io.segmentme.redis.dto.out.AnalysisResponeMessageOut;
import lombok.experimental.UtilityClass;

import java.util.stream.Collectors;

@UtilityClass
public class RedisMessageOutConverter {

    public AnalysisResponeMessageOut of(SdkAnalysisResponse response) {
        AnalysisResponeMessageOut messageOut = new AnalysisResponeMessageOut();
        messageOut.setBody(new AnalysisResponse()
                .setContextId(response.getContextId())
                .setAnalyzedSegments(response.getAnalyzedSegments().stream().map(RedisMessageOutConverter::of).collect(Collectors.toList())));
        return messageOut;
    }

    private SegmentAnalysisResutl of(SegmentAnalysisResult result) {
        return new SegmentAnalysisResutl()
                .setAnalysisTime(result.getAnalysisTime())
                .setName(result.getName())
                .setValue(result.isValue());
    }
}

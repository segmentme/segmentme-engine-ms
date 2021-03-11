package io.segmentme.analysis.service.converter;

import io.segmentme.analysis.dto.AnalysisResult;
import io.segmentme.analysis.dto.SegmentAnalysisResult;
import io.segmentme.redis.dto.AnalysisResponse;
import io.segmentme.redis.dto.SegmentAnalysisResutl;
import io.segmentme.redis.dto.out.AnalysisResponeMessageOut;
import lombok.experimental.UtilityClass;

import java.util.stream.Collectors;

@UtilityClass
public class RedisMessageOutConverter {

    public AnalysisResponeMessageOut of(AnalysisResult response) {
        AnalysisResponeMessageOut messageOut = new AnalysisResponeMessageOut();
        messageOut.setBody(new AnalysisResponse()
            .setAnalyzedSegments(response.getSegmentAnalysisResults().stream().map(RedisMessageOutConverter::of).collect(Collectors.toList())));
        return messageOut;
    }

    private SegmentAnalysisResutl of(SegmentAnalysisResult result) {
        return new SegmentAnalysisResutl()
            .setAnalysisTime(result.getAnalysisTime())
            .setName(result.getName())
            .setValue(result.isValue());
    }
}

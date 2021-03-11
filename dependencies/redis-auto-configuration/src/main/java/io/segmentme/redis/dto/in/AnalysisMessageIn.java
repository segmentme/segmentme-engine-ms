package io.segmentme.redis.dto.in;

import io.segmentme.redis.dto.AnalysisRequest;
import io.segmentme.redis.dto.RedisMessageType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

@Data
@EqualsAndHashCode(callSuper = true)
public class AnalysisMessageIn extends RedisMessageIn<AnalysisRequest> {

    private final RedisMessageType type = RedisMessageType.ANALYSIS_MESSAGE_IN;

    @NotEmpty
    private String integrationPointKey;

}

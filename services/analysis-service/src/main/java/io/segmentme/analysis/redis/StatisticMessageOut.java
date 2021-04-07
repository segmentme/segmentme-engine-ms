package io.segmentme.analysis.redis;

import io.segmentme.analysis.dto.CollectedAnalysysStatisticDto;
import io.segmentme.redis.dto.RedisMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class StatisticMessageOut extends CollectedAnalysysStatisticDto implements RedisMessage {
}
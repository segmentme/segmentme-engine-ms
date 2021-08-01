package io.segmentme.access.service.domain.statistic;

import io.segmentme.core.domain.DbDomain;
import lombok.Data;

@Data
public class SegmentStatisticCount implements DbDomain {
    private String segmentId;
    private int count;
}

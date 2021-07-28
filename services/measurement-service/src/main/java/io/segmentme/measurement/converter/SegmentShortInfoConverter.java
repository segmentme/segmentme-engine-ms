package io.segmentme.measurement.converter;

import io.segmentme.core.domain.segment.Segment;
import io.segmentme.measurement.dto.analysis.SegmentDto;
import io.segmentme.measurement.dto.analysis.SegmentShortInfo;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SegmentShortInfoConverter {

    public SegmentShortInfo of(Segment source) {
        return new SegmentShortInfo()
            .setId(source.getId())
            .setIntegrationPointKey(source.getIntegrationPointKey())
            .setName(source.getName());
    }

    public SegmentShortInfo of(SegmentDto source) {
        return new SegmentShortInfo()
            .setId(source.getId())
            .setIntegrationPointKey(source.getIntegrationPointKey())
            .setName(source.getName());
    }
}

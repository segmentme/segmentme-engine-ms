package io.segmentme.measurement.service.converter;

import io.segmentme.measurement.service.domain.segment.Segment;
import io.segmentme.measurement.service.dto.analysis.SegmentDto;
import io.segmentme.measurement.service.dto.analysis.SegmentShortInfo;
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

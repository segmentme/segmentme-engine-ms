package io.segmentme.access.service.converter;

import io.segmentme.access.service.domain.segment.Segment;
import io.segmentme.access.service.dto.analysis.SegmentDto;
import io.segmentme.access.service.dto.analysis.SegmentShortInfo;
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

package io.segmentme.management.service.converter;

import io.segmentme.analysis.dto.segment.SegmentDto;
import io.segmentme.analysis.dto.segment.SegmentShortInfo;
import io.segmentme.core.domain.segment.Segment;
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

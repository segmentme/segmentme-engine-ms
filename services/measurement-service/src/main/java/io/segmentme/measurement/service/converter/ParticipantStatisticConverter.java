package io.segmentme.measurement.service.converter;

import io.segmentme.measurement.service.domain.statistic.ParticipantStatistic;
import io.segmentme.measurement.service.dto.statistic.ParticipantStatisticDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ParticipantStatisticConverter {

    public ParticipantStatisticDto toDto(ParticipantStatistic statistic) {
        if (statistic == null) {
            return null;
        }

        return new ParticipantStatisticDto()
                .setId(statistic.getId())
                .setInSegment(statistic.getInSegment());
    }
}
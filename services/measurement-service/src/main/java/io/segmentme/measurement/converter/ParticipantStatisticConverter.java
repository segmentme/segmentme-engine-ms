package io.segmentme.measurement.converter;

import io.segmentme.measurement.domain.ParticipantStatistic;
import io.segmentme.measurement.dto.statistic.ParticipantStatisticDto;
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
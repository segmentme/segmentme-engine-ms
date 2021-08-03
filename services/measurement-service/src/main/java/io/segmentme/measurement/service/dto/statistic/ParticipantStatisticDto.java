package io.segmentme.measurement.service.dto.statistic;

import lombok.Data;

import java.util.List;

@Data
public class ParticipantStatisticDto {

    private String id;

    private List<String> inSegment;
}
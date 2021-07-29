package io.segmentme.analysis.service.clients.dto;

import lombok.Data;

import java.util.List;

@Data
public class ParticipantStatisticDto {

    private String id;

    private List<String> inSegment;
}
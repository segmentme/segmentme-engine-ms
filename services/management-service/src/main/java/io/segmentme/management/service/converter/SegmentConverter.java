package io.segmentme.management.service.converter;

import io.segmentme.analysis.dto.segment.SegmentDto;
import io.segmentme.management.service.domain.segment.Segment;
import lombok.experimental.UtilityClass;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@UtilityClass
public class SegmentConverter {

    public Segment of(SegmentDto source, String contextId, String integrationPointKey) {
        return (Segment) new Segment()
                .setContextId(contextId)
                .setIntegrationPointKey(integrationPointKey)
                .setName(source.getName())
                .setActive(source.isActive())
                .setAggregation(source.getAggregation())
                .setMatchResult(source.isMatchResult())
                .setConditions(source.getConditions().stream().map(ConditionConverter::of).collect(Collectors.toList()))
                .setHash(Optional.ofNullable(source.getHash()).orElseGet(() -> UUID.randomUUID().toString()))
                .setDescription(source.getDescription())
                .setOpenedForPercentage(source.getOpenedForPercentage())
                .setId(source.getId());
    }

    public SegmentDto of(Segment source) {
        return new SegmentDto()
                .setId(source.getId())
                .setName(source.getName())
                .setHash(source.getHash())
                .setActive(source.isActive())
                .setAggregation(source.getAggregation())
                .setMatchResult(source.isMatchResult())
                .setDescription(source.getDescription())
                .setIntegrationPointKey(source.getIntegrationPointKey())
                .setOpenedForPercentage(source.getOpenedForPercentage())
                .setConditions(source.getConditions().stream().map(ConditionConverter::of).collect(Collectors.toList()));
    }
}

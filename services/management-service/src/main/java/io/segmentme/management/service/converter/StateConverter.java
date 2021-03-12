package io.segmentme.management.service.converter;

import io.segmentme.analysis.dto.state.StateDto;
import io.segmentme.core.domain.state.State;
import io.segmentme.helpers.dao.service.SegmentService;
import io.segmentme.management.service.exception.SegmentManagerException;
import io.segmentme.management.service.exception.error.SegmentMangerErrors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StateConverter {

    private final SegmentService segmentService;

    public State of(StateDto target) {
        return (State) new State()
            .setName(target.getName())
            .setSegment(segmentService.findById(target.getSegment().getId()).orElseThrow(() -> new SegmentManagerException(SegmentMangerErrors.SEGMENT_NOT_FOUND)))
            .setValue(target.getValue())
            .setDefaultValue(target.getDefaultValue())
            .setIntegrationPointKey(target.getIntegrationPointKey())
            .setId(target.getId());
    }

    public StateDto of(State target) {
        return new StateDto()
            .setName(target.getName())
            .setSegment(SegmentShortInfoConverter.of(target.getSegment()))
            .setValue(target.getValue())
            .setDefaultValue(target.getDefaultValue())
            .setIntegrationPointKey(target.getIntegrationPointKey())
            .setId(target.getId());
    }
}

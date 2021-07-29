package io.segmentme.access.service.service;

import io.segmentme.access.service.domain.state.State;
import io.segmentme.access.service.repository.StateRepository;
import io.segmentme.core.db.service.AbstractDatabaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StateService extends AbstractDatabaseService<State, StateRepository> {


}

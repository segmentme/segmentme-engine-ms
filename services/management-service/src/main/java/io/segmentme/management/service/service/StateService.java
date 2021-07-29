package io.segmentme.management.service.service;

import io.segmentme.core.db.service.AbstractDatabaseService;
import io.segmentme.management.service.domain.state.State;
import io.segmentme.management.service.repository.StateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StateService extends AbstractDatabaseService<State, StateRepository> {


    public Optional<State> findByNameAndIntegrationPointKey(String name, String integrationPointKey) {
        return repository.findByNameAndIntegrationPointKey(name, integrationPointKey);
    }

    public List<State> findByIntegrationPointKeys(Iterable<String> integrationPointKey) {
        return repository.findByIntegrationPointKeyIn(integrationPointKey);
    }
}

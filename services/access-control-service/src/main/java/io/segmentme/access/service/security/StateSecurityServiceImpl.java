package io.segmentme.access.service.security;

import io.segmentme.access.service.repository.UserRepository;
import io.segmentme.core.domain.DbObject;
import io.segmentme.core.domain.state.State;
import io.segmentme.helpers.dao.service.StateService;
import io.segmentme.security.StateSecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StateSecurityServiceImpl implements StateSecurityService {

    private final StateService stateService;

    private final SecurityService securityService;

    private final UserRepository userRepository;

    @Override
    public boolean isManagedState(String stateId, String userId) {
        String id = userRepository.findByExternalId(userId).map(DbObject::getId).orElse(null);
        return stateService.findById(stateId)
            .map(State::getIntegrationPointKey)
            .map(it -> securityService.isValidIntegrationPointKey(it, id))
            .orElse(false);
    }
}

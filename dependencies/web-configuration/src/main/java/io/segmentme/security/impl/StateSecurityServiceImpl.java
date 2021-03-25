package io.segmentme.security.impl;

import io.segmentme.security.StateSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StateSecurityServiceImpl implements StateSecurityService {
    @Qualifier("access-control-service")
    private final WebClient accessControlClient;


    @Override
    public boolean isManagedState(String stateId, String userId) {
        Boolean result = Objects.requireNonNull(accessControlClient.get().uri("/state/has-access?stateId=" + stateId).retrieve().toEntity(Boolean.class).block()).getBody();
        if (result == null) {
            return false;
        }
        return result;
    }
}

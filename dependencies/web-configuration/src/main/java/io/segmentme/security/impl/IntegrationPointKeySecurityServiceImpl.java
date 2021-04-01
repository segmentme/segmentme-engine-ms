package io.segmentme.security.impl;

import io.segmentme.security.IntegrationPointKeySecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class IntegrationPointKeySecurityServiceImpl implements IntegrationPointKeySecurityService {

    @Qualifier("access-control-service")
    private final WebClient accessControlClient;

    @Override
    public boolean isManaged(String userId, String... integrationPointKeys) {
        return Optional.ofNullable(accessControlClient.post()
                .uri("/integration-point-key/has-access")
                .bodyValue(integrationPointKeys)
                .retrieve()
                .toEntity(Boolean.class)
                .block())
                .map(HttpEntity::getBody)
                .map(Boolean.TRUE::equals)
                .orElse(false);
    }
}

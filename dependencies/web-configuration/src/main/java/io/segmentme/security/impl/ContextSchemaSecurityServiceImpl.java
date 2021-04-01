package io.segmentme.security.impl;

import io.segmentme.security.ContextSchemaSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

@RequiredArgsConstructor
@Service(value = "contextSchemaSecurityService")
public class ContextSchemaSecurityServiceImpl implements ContextSchemaSecurityService {
    @Qualifier("access-control-service")
    private final WebClient accessControlClient;

    @Override
    public boolean isManagedSchema(String contextSchemaId) {
        Boolean result = Objects.requireNonNull(accessControlClient.get().uri("/context-schema/has-access?contextSchemaId=" + contextSchemaId).retrieve().toEntity(Boolean.class).block()).getBody();
        if (result == null) {
            return false;
        }
        return result;
    }
}

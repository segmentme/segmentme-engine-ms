package io.segmentme.management.service.security;

import io.segmentme.core.domain.context.ContextSchema;
import io.segmentme.helpers.dao.service.ContextSchemaService;
import io.segmentme.web.configuration.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContextSchemaSecurityService {

    private final ContextSchemaService contextSchemaService;

    private final SecurityService securityService;

    public boolean isManagedSchema(String contextSchemaId) {
        return contextSchemaService.findById(contextSchemaId)
            .map(ContextSchema::getIntegrationPointKey)
            .map(it -> securityService.isValidIntegrationPointKey(it, SecurityUtils.currentUserId()))
            .orElse(false);
    }
}


package io.segmentme.access.service.security;

import io.segmentme.access.service.repository.UserRepository;
import io.segmentme.core.domain.DbObject;
import io.segmentme.core.domain.workpsace.UserProfile;
import io.segmentme.core.domain.workpsace.Workspace;
import io.segmentme.helpers.dao.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityService {

    private final WorkspaceService workspaceService;
    private final UserRepository userService;

    public boolean isValidIntegrationPointKeys(String[] integrationPointKeys, String userId) {
        return Arrays.stream(integrationPointKeys).allMatch(it -> isValidIntegrationPointKey(it, userId));
    }

    public boolean isValidIntegrationPointKey(String integrationPointKey, String externalId) {
        String systemUserId = userService.findByExternalId(externalId).map(DbObject::getId).orElse(null);
        return workspaceService.findByIntegrationPointKey(integrationPointKey)
            .map(Workspace::getUserProfiles)
            .stream()
            .flatMap(Collection::stream)
            .map(UserProfile::getUserId)
            .filter(it -> it.equalsIgnoreCase(systemUserId))
            .findFirst()
            .map(it -> Boolean.TRUE)
            .orElse(false);
    }
}

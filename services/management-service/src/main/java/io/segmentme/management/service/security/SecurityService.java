package io.segmentme.management.service.security;

import io.segmentme.core.domain.DbObject;
import io.segmentme.core.domain.workpsace.UserProfile;
import io.segmentme.core.domain.workpsace.Workspace;
import io.segmentme.helpers.dao.service.WorkspaceService;
import io.segmentme.management.service.service.user.UserService;
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
    private final UserService userService;

    public boolean isValidIntegrationPointKeys(String[] integrationPointKeys, String userId) {
        return Arrays.stream(integrationPointKeys).allMatch(it -> isValidIntegrationPointKey(it, userId));
    }

    public boolean isValidIntegrationPointKey(String integrationPointKey, String userId) {
        String systemUserId = userService.findByExternalId(userId).map(DbObject::getId).orElse(null);
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

package io.segmentme.security.impl;

import io.segmentme.security.WorkspaceSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class WorkspaceSecurityServiceImpl implements WorkspaceSecurityService {
    @Qualifier("access-control-service")
    private final WebClient accessControlClient;

    @Override
    public boolean isWorkspaceMember(String workspaceId) {
        Boolean result = Objects.requireNonNull(accessControlClient.get().uri("/workspace/has-access?workspaceId=" + workspaceId).retrieve().toEntity(Boolean.class).block()).getBody();
        if (result == null) {
            return false;
        }
        return result;
    }
}

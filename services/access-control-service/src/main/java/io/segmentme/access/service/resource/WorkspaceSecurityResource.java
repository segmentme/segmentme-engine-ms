package io.segmentme.access.service.resource;

import io.segmentme.security.WorkspaceSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/workspace")
@RequiredArgsConstructor
public class WorkspaceSecurityResource {

    private final WorkspaceSecurityService workspaceSecurityService;


    @GetMapping("/has-access")
    public Boolean hasAccess(@RequestParam String workspaceId) {
        return workspaceSecurityService.isWorkspaceMember(workspaceId);
    }

}

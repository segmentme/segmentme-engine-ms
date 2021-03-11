package io.segmentme.management.service.resource;

import io.segmentme.core.domain.workpsace.WorkspaceConfiguration;
import io.segmentme.management.service.dto.workspace.WorkspaceDatesValidationRequest;
import io.segmentme.management.service.dto.workspace.WorkspaceDatesValidationResponse;
import io.segmentme.management.service.dto.workspace.WorkspaceDetails;
import io.segmentme.management.service.dto.workspace.WorkspaceUserProfile;
import io.segmentme.management.service.facade.WorkspaceFacade;
import io.segmentme.models.shared.analysis.IntegrationPoint;
import io.segmentme.web.configuration.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/workspace")
@PreAuthorize("@workspaceSecurityService.isWorkspaceMember(#workspaceId)")
public class WorkspaceController {

    private final WorkspaceFacade workspaceFacade;

    @GetMapping("/{workspaceId}")
    public WorkspaceDetails getWorkspace(@PathVariable String workspaceId) {
        return workspaceFacade.getWorkspaceDetails(workspaceId);
    }

    @PostMapping
    @PreAuthorize("permitAll()")
    public WorkspaceDetails createWorkspace(@AuthenticationPrincipal AuthUser currentUser,
                                            @RequestParam String name) {
        return workspaceFacade.createWorkspace(currentUser.getId(), name);
    }

    @PostMapping("/{workspaceId}/integration-point")
    public IntegrationPoint createIntegrationPoint(@PathVariable String workspaceId, @RequestParam String name) {
        return workspaceFacade.addIntegrationPoint(workspaceId, name);
    }

    @PutMapping("/{workspaceId}/configuration")
    public void updateWorkspaceConfiguration(@PathVariable String workspaceId, @RequestBody WorkspaceConfiguration workspaceConfiguration) {
        workspaceFacade.updateConfiguration(workspaceId, workspaceConfiguration);
    }

    @PutMapping("/{workspaceId}")
    public void updateWorkspaceConfiguration(@PathVariable String workspaceId, @RequestBody WorkspaceDetails workspaceDetails) {
        workspaceFacade.updateWorkspace(workspaceId, workspaceDetails);
    }

    @PostMapping("/{workspaceId}/configuration/date-format/validate")
    public WorkspaceDatesValidationResponse validateWorkspaceConfiguration(@PathVariable String workspaceId, @RequestBody WorkspaceDatesValidationRequest validationRequest) {
        return workspaceFacade.validateDateFormats(validationRequest);
    }

    @PutMapping("/{workspaceId}/integration-point")
    public void updateIntegrationPoint(@PathVariable String workspaceId, @RequestBody IntegrationPoint integrationPoint) {
        workspaceFacade.updateIntegrationPoint(workspaceId, integrationPoint);
    }

    @DeleteMapping("/{workspaceId}/integration-point/{key}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteIntegrationPoint(@PathVariable String workspaceId, @PathVariable String key) {
        workspaceFacade.removeIntegrationPoint(workspaceId, key);
    }

    @DeleteMapping("/{workspaceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWorkspace(@PathVariable String workspaceId) {
        workspaceFacade.removeWorkspace(workspaceId);
    }


    @GetMapping("/{workspaceId}/profiles")
    public List<WorkspaceUserProfile> getWorkspaceList(@PathVariable String workspaceId) {
        return workspaceFacade.getWorkspaceProfiles(workspaceId);
    }

}

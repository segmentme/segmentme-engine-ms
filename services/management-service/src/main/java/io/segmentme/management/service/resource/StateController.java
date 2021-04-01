package io.segmentme.management.service.resource;

import io.segmentme.analysis.dto.state.StateDto;
import io.segmentme.management.service.service.state.StateManager;
import io.segmentme.web.configuration.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/state")
@RequiredArgsConstructor
public class StateController {

    private final StateManager stateManager;

    @PostMapping
    @PreAuthorize("@integrationPointKeySecurityService.isManaged(#currentUser.id, #state.integrationPointKey)")
    public StateDto create(@AuthenticationPrincipal AuthUser currentUser,
                           @Valid @RequestBody StateDto state) {
        state.setId(null);
        return stateManager.create(state);
    }

    @GetMapping("/workspace/{workspaceId}")
    @PreAuthorize("@workspaceSecurityService.isWorkspaceMember(#workspaceId)")
    public List<StateDto> getAllInWorkspace(@PathVariable String workspaceId) {
        return stateManager.getByWorkspaceId(workspaceId);
    }

    @GetMapping("/{stateId}")
    @PreAuthorize("@stateSecurityService.isManagedState(#stateId, #currentUser.id)")
    public StateDto getById(@AuthenticationPrincipal AuthUser currentUser,
                            @PathVariable String stateId) {
        return stateManager.getById(stateId);
    }

    @PutMapping("/{stateId}")
    @PreAuthorize("@stateSecurityService.isManagedState(#stateId, #currentUser.id)")
    public StateDto update(@AuthenticationPrincipal AuthUser currentUser,
                           @PathVariable String stateId,
                           @Valid @RequestBody StateDto state) {
        return stateManager.update(stateId, state);
    }

    @DeleteMapping("/{stateId}")
    @PreAuthorize("@stateSecurityService.isManagedState(#stateId, #currentUser.id)")
    public void delete(@AuthenticationPrincipal AuthUser currentUser,
                       @PathVariable String stateId) {
        stateManager.delete(stateId);
    }
}

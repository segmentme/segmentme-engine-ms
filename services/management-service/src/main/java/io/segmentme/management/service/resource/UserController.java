package io.segmentme.management.service.resource;

import io.segmentme.management.service.dto.user.UserDetails;
import io.segmentme.management.service.facade.UserFacade;
import io.segmentme.web.configuration.AuthUser;
import io.segmentme.web.configuration.auth.Auth0Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserFacade userFacade;

    private final Auth0Service auth0Service;

    @GetMapping
    public UserDetails getCurrentUserDetails(@AuthenticationPrincipal AuthUser authUser) {
        return userFacade.getUserDetails(authUser.getExternalId());
    }

    @PutMapping
    @PreAuthorize("@workspaceSecurityService.isWorkspaceMember(#workspaceId)")
    public void switchWorkspace(@RequestParam String workspaceId,
                                @AuthenticationPrincipal AuthUser authUser) {
        userFacade.switchWorkspace(authUser.getId(), workspaceId);
    }

    @GetMapping("/resend-verification-email")
    public void resendVerificationEmail(@AuthenticationPrincipal AuthUser authUser) {
        auth0Service.resendVerificationEmail(authUser.getId());
    }
}

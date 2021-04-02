package io.segmentme.access.service.resource;

import io.segmentme.security.StateSecurityService;
import io.segmentme.web.configuration.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/state")
@RequiredArgsConstructor
public class StateSecurityResource {

    private final StateSecurityService stateSecurityService;


    @GetMapping("/has-access")
    public Boolean hasAccess(@RequestParam String stateId) {
        return stateSecurityService.isManagedState(stateId, SecurityUtils.currentExternalUserId());
    }

}

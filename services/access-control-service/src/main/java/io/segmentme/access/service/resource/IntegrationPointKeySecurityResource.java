package io.segmentme.access.service.resource;

import io.segmentme.access.service.security.SecurityService;
import io.segmentme.web.configuration.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/integration-point-key")
public class IntegrationPointKeySecurityResource {

    private final SecurityService securityService;

    @PostMapping("/has-access")
    public Boolean hasAccess(@RequestBody String[] integrationPointKeys) {
        return securityService.isValidIntegrationPointKeys(integrationPointKeys, SecurityUtils.currentExternalUserId());
    }
}

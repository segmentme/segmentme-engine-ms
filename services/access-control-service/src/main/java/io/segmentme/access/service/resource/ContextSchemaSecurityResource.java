package io.segmentme.access.service.resource;

import io.segmentme.security.ContextSchemaSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/context-schema")
@RequiredArgsConstructor
public class ContextSchemaSecurityResource {

    private final ContextSchemaSecurityService contextSchemaSecurityService;

    @GetMapping("/has-access")
    public Boolean hasAccess(@RequestParam String contextSchemaId) {
        return contextSchemaSecurityService.isManagedSchema(contextSchemaId);
    }

}

package io.segmentme.access.service.resource;

import io.segmentme.security.SegmentSecurityService;
import io.segmentme.web.configuration.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/segment")
@RequiredArgsConstructor
public class SegmentSecurityResource {

    private final SegmentSecurityService contextSchemaSecurityService;

    @GetMapping("/has-access")
    public Boolean hasAccess(@RequestParam String segmentId) {
        return contextSchemaSecurityService.isManaged(segmentId, SecurityUtils.currentUserId());
    }

}

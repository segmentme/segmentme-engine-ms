package io.segmentme.management.service.service.clients.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ParticipantAcknowledgeRequest {
    private String contextId;
    private Map<String, Object> values;
    private String uniquenessIndicator;
}

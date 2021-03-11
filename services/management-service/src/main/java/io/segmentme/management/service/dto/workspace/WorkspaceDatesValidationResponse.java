package io.segmentme.management.service.dto.workspace;

import lombok.Data;

import java.util.Map;

@Data
public class WorkspaceDatesValidationResponse {
    private Map<String, Boolean> formats;
    private Map<String, String> dates;


}

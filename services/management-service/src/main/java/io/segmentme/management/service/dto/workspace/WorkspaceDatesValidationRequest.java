package io.segmentme.management.service.dto.workspace;

import lombok.Data;

import java.util.List;

@Data
public class WorkspaceDatesValidationRequest {
    private List<String> formats;
    private List<String> datesToValidate;
}

package io.segmentme.management.service.dto;

import lombok.Data;

import java.util.List;

@Data
public class SegmentExportRequest {
    private String workspaceId;

    private List<String> segmentIds;
}

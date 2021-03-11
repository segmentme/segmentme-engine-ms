package io.segmentme.analysis.api.dto;

import lombok.Data;

@Data
public class ContextSchemaShortInfo {
    private String integrationPointKey;

    private String name;

    private String id;

    private String hash;
}

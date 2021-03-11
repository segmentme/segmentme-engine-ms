package io.segmentme.management.service.dto.user;

import lombok.Data;

@Data
public class UserHolder {
    private String id;

    private String externalId;

    private String email;

    private String name;

    private String lastActiveWorkspace;
}

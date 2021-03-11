package io.segmentme.management.service.dto.user;

import lombok.Data;

import java.util.List;

@Data
public class UserDetails {
    private UserBasicInfo userBasicInfo;
    private List<CurrentUserProfile> profiles;
}

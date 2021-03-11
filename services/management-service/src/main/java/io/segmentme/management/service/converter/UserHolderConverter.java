package io.segmentme.management.service.converter;

import io.segmentme.management.domain.user.User;
import io.segmentme.management.service.dto.user.UserHolder;

public class UserHolderConverter {
    private UserHolderConverter() {
    }

    public static User toUser(UserHolder holder) {
        return (User) new User()
            .setEmail(holder.getEmail())
            .setName(holder.getName())
            .setLastActiveWorkspace(holder.getLastActiveWorkspace())
            .setExternalId(holder.getExternalId())
            .setId(holder.getId());
    }

    public static UserHolder toHolder(User user) {
        return new UserHolder()
            .setEmail(user.getEmail())
            .setName(user.getName())
            .setLastActiveWorkspace(user.getLastActiveWorkspace())
            .setId(user.getId());
    }
}

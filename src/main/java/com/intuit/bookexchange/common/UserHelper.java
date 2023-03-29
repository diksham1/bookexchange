package com.intuit.bookexchange.common;

import java.net.URI;

import com.intuit.bookexchange.dto.UserResponse;
import com.intuit.bookexchange.entities.User;

public class UserHelper {
    public static UserResponse buildUserResponse(User user) {
        return UserResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .rewardPoints(user.getRewardPoints())
                .rating(user.getRating())
                .build();
    }

    public static URI getUserURI(User user) {
        return URI.create(String.format("/users/%d", user.getId()));
    }
}

package com.intuit.bookexchange.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private int userId;
    private String username;
    private String email;
    private int rewardPoints;
    private double rating;
}

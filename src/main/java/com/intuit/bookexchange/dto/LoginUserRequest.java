package com.intuit.bookexchange.dto;

import lombok.Data;

@Data
public class LoginUserRequest {
    private String username;
    private String password;
}

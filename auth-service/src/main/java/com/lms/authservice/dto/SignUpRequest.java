package com.lms.authservice.dto;

import lombok.Data;

@Data
public class SignUpRequest {
    private String email;
    private String password;
}

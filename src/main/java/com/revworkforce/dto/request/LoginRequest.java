package com.revworkforce.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
//login validation
public class LoginRequest {
    @NotBlank @Email
    private String email;
    @NotBlank
    private String password;
}

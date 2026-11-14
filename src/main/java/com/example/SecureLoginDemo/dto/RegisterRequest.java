package com.example.SecureLoginDemo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterRequest {

    @NotNull(message="Username is required")
    private String username;

    @NotNull(message="Email is required")
    @Email(message="Please provide a valid email")
    private String email;

    @NotNull(message="Password is required")
    @Size(min = 8, message="Password must be at least 8 character")
    private String password;

}

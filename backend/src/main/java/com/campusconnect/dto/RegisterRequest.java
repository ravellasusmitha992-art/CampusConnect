package com.campusconnect.dto;

import com.campusconnect.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RegisterRequest {

    @NotBlank
    private String fullName;

    @NotBlank @Email
    private String email;

    @NotBlank
    private String password;

    @NotNull
    private Role role; // STUDENT, FACULTY, ADMIN

    private String department;
}

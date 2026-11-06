package com.campusconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileUpdateRequest {
    @NotBlank(message = "Full name is required")
    @Size(max = 120)
    private String fullName;

    @Size(max = 20)
    private String phone;

    @Size(max = 500)
    private String bio;

    @Size(max = 100)
    private String department;

    @Size(max = 100)
    private String course;

    @Size(max = 30)
    private String studyYear;
}

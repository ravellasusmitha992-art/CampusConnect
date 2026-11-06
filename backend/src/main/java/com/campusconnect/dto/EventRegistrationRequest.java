package com.campusconnect.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventRegistrationRequest {
    @Pattern(regexp = "^$|^[+]?[0-9\\s()-]{7,20}$", message = "Enter a valid phone number")
    private String phone;

    @Size(max = 100, message = "Team name must be 100 characters or fewer")
    private String teamName;

    @Size(max = 1000, message = "Notes must be 1000 characters or fewer")
    private String notes;
}

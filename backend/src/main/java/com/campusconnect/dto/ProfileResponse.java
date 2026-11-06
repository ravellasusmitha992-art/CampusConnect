package com.campusconnect.dto;

import com.campusconnect.entity.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileResponse {
    private Long id;
    private String fullName;
    private String email;
    private Role role;
    private String department;
    private String phone;
    private String bio;
    private String course;
    private String studyYear;
}

package com.campusconnect.dto;

import com.campusconnect.entity.EventRegistration;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class EventParticipantResponse {
    private final Long registrationId;
    private final Long userId;
    private final String fullName;
    private final String email;
    private final String department;
    private final String phone;
    private final String teamName;
    private final String notes;
    private final LocalDateTime registeredAt;

    public EventParticipantResponse(EventRegistration registration) {
        this.registrationId = registration.getId();
        this.userId = registration.getUser().getId();
        this.fullName = registration.getUser().getFullName();
        this.email = registration.getUser().getEmail();
        this.department = registration.getUser().getDepartment();
        this.phone = registration.getPhone();
        this.teamName = registration.getTeamName();
        this.notes = registration.getNotes();
        this.registeredAt = registration.getRegisteredAt();
    }
}

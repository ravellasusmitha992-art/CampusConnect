package com.campusconnect.dto;

import com.campusconnect.entity.Event;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class EventResponse {
    private final Long id;
    private final String title;
    private final String description;
    private final String venue;
    private final String posterUrl;
    private final LocalDateTime eventDate;
    private final LocalDateTime createdAt;
    private final String organizer;
    private final Long organizerId;
    private final long participantCount;
    private final boolean registeredByMe;

    public EventResponse(Event event, long participantCount, boolean registeredByMe) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.description = event.getDescription();
        this.venue = event.getVenue();
        this.posterUrl = event.getPosterUrl();
        this.eventDate = event.getEventDate();
        this.createdAt = event.getCreatedAt();
        this.organizer = event.getCreatedBy() == null ? null : event.getCreatedBy().getFullName();
        this.organizerId = event.getCreatedBy() == null ? null : event.getCreatedBy().getId();
        this.participantCount = participantCount;
        this.registeredByMe = registeredByMe;
    }
}

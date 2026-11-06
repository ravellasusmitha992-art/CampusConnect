package com.campusconnect.controller;

import com.campusconnect.dto.EventParticipantResponse;
import com.campusconnect.dto.EventRegistrationRequest;
import com.campusconnect.entity.Event;
import com.campusconnect.entity.EventRegistration;
import com.campusconnect.entity.Role;
import com.campusconnect.repository.EventRegistrationRepository;
import com.campusconnect.repository.EventRepository;
import com.campusconnect.service.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events/{eventId}/registrations")
@RequiredArgsConstructor
public class EventRegistrationController {

    private final EventRepository eventRepository;
    private final EventRegistrationRepository registrationRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<?> register(@PathVariable Long eventId,
                                      @Valid @RequestBody EventRegistrationRequest request,
                                      @AuthenticationPrincipal CustomUserDetails principal) {
        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) {
            return ResponseEntity.notFound().build();
        }
        if (principal.getUser().getRole() != Role.STUDENT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Only students can register for events."));
        }
        if (event.getEventDate() != null && event.getEventDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Registration is closed for past events."));
        }
        Long userId = principal.getUser().getId();
        if (registrationRepository.existsByEvent_IdAndUser_Id(eventId, userId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "You are already registered for this event."));
        }

        EventRegistration registration = EventRegistration.builder()
                .event(event)
                .user(principal.getUser())
                .phone(blankToNull(request.getPhone()))
                .teamName(blankToNull(request.getTeamName()))
                .notes(blankToNull(request.getNotes()))
                .build();
        registrationRepository.save(registration);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "You are registered for this event."));
    }

    @DeleteMapping
    @Transactional
    public ResponseEntity<?> cancel(@PathVariable Long eventId,
                                    @AuthenticationPrincipal CustomUserDetails principal) {
        return registrationRepository.findByEvent_IdAndUser_Id(eventId, principal.getUser().getId())
                .map(registration -> {
                    registrationRepository.delete(registration);
                    return ResponseEntity.ok(Map.of("message", "Your registration has been cancelled."));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<?> getParticipants(@PathVariable Long eventId,
                                             @AuthenticationPrincipal CustomUserDetails principal) {
        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) {
            return ResponseEntity.notFound().build();
        }
        boolean isOrganizer = event.getCreatedBy() != null
                && event.getCreatedBy().getId().equals(principal.getUser().getId());
        boolean isAdmin = principal.getUser().getRole() == Role.ADMIN;
        if (!isOrganizer && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Only the event organizer or an admin can view participants."));
        }
        List<EventParticipantResponse> participants = registrationRepository
                .findAllByEvent_IdOrderByRegisteredAtAsc(eventId)
                .stream().map(EventParticipantResponse::new).toList();
        return ResponseEntity.ok(participants);
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

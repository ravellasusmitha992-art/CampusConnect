package com.campusconnect.controller;

import com.campusconnect.dto.EventRequest;
import com.campusconnect.dto.EventResponse;
import com.campusconnect.entity.Event;
import com.campusconnect.entity.User;
import com.campusconnect.repository.EventRegistrationRepository;
import com.campusconnect.repository.EventRepository;
import com.campusconnect.service.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventRepository eventRepository;
    private final EventRegistrationRepository registrationRepository;

    @GetMapping
    public List<EventResponse> getAllEvents(@AuthenticationPrincipal CustomUserDetails principal) {
        return eventRepository.findAll().stream().map(event -> toResponse(event, principal)).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEvent(@PathVariable Long id,
                                                   @AuthenticationPrincipal CustomUserDetails principal) {
        return eventRepository.findById(id)
                .map(event -> ResponseEntity.ok(toResponse(event, principal)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventRequest request,
                                                      @AuthenticationPrincipal CustomUserDetails principal) {
        User faculty = principal.getUser();
        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .venue(request.getVenue())
                .posterUrl(request.getPosterUrl())
                .eventDate(request.getEventDate())
                .createdBy(faculty)
                .build();
        return ResponseEntity.ok(toResponse(eventRepository.save(event), principal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable Long id,
                                                      @Valid @RequestBody EventRequest request,
                                                      @AuthenticationPrincipal CustomUserDetails principal) {
        return eventRepository.findById(id).map(event -> {
            event.setTitle(request.getTitle());
            event.setDescription(request.getDescription());
            event.setVenue(request.getVenue());
            event.setPosterUrl(request.getPosterUrl());
            event.setEventDate(request.getEventDate());
            return ResponseEntity.ok(toResponse(eventRepository.save(event), principal));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        if (!eventRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        eventRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private EventResponse toResponse(Event event, CustomUserDetails principal) {
        Long userId = principal == null ? null : principal.getUser().getId();
        boolean registered = userId != null
                && registrationRepository.existsByEvent_IdAndUser_Id(event.getId(), userId);
        long count = registrationRepository.countByEvent_Id(event.getId());
        return new EventResponse(event, count, registered);
    }
}

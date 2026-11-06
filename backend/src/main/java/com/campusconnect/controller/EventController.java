package com.campusconnect.controller;

import com.campusconnect.dto.EventRequest;
import com.campusconnect.entity.Event;
import com.campusconnect.entity.User;
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

    @GetMapping
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEvent(@PathVariable Long id) {
        return eventRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@Valid @RequestBody EventRequest request,
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
        return ResponseEntity.ok(eventRepository.save(event));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @Valid @RequestBody EventRequest request) {
        return eventRepository.findById(id).map(event -> {
            event.setTitle(request.getTitle());
            event.setDescription(request.getDescription());
            event.setVenue(request.getVenue());
            event.setPosterUrl(request.getPosterUrl());
            event.setEventDate(request.getEventDate());
            return ResponseEntity.ok(eventRepository.save(event));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        eventRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<Event> joinEvent(@PathVariable Long id,
                                            @AuthenticationPrincipal CustomUserDetails principal) {
        return eventRepository.findById(id).map(event -> {
            event.getParticipants().add(principal.getUser());
            return ResponseEntity.ok(eventRepository.save(event));
        }).orElse(ResponseEntity.notFound().build());
    }
}

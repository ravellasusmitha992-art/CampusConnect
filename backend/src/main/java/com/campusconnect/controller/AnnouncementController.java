package com.campusconnect.controller;

import com.campusconnect.dto.AnnouncementRequest;
import com.campusconnect.entity.Announcement;
import com.campusconnect.entity.User;
import com.campusconnect.repository.AnnouncementRepository;
import com.campusconnect.service.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementRepository announcementRepository;

    @GetMapping
    public List<Announcement> getAll() {
        return announcementRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Announcement> create(@Valid @RequestBody AnnouncementRequest request,
                                                @AuthenticationPrincipal CustomUserDetails principal) {
        User faculty = principal.getUser();
        Announcement announcement = Announcement.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .posterUrl(request.getPosterUrl())
                .postedBy(faculty)
                .build();
        return ResponseEntity.ok(announcementRepository.save(announcement));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        announcementRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

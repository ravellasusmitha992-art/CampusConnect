package com.campusconnect.controller;
import com.campusconnect.dto.AnnouncementRequest;
import com.campusconnect.entity.*;
import com.campusconnect.repository.AnnouncementRepository;
import com.campusconnect.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/announcements") @RequiredArgsConstructor
public class AnnouncementController {
 private final AnnouncementRepository announcementRepository;
 private final NotificationService notificationService;
 @GetMapping public List<Announcement> getAll(){ return announcementRepository.findAll(); }
 @PostMapping public ResponseEntity<Announcement> create(@Valid @RequestBody AnnouncementRequest request,@AuthenticationPrincipal CustomUserDetails principal){
  User faculty=principal.getUser();
  Announcement announcement=Announcement.builder().title(request.getTitle()).content(request.getContent()).posterUrl(request.getPosterUrl()).postedBy(faculty).build();
  Announcement saved=announcementRepository.save(announcement);
  notificationService.createAnnouncementNotifications(saved,faculty.getId());
  return ResponseEntity.ok(saved);
 }
 @DeleteMapping("/{id}") public ResponseEntity<?> delete(@PathVariable Long id){ announcementRepository.deleteById(id); return ResponseEntity.noContent().build(); }
}

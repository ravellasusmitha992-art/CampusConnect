package com.campusconnect.controller;
import com.campusconnect.dto.NotificationResponse;
import com.campusconnect.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/notifications") @RequiredArgsConstructor
public class NotificationController {
 private final NotificationService notificationService;
 @GetMapping public List<NotificationResponse> getMine(@AuthenticationPrincipal CustomUserDetails p){ return notificationService.getForUser(p.getUser().getId()); }
 @GetMapping("/unread-count") public Map<String,Long> unreadCount(@AuthenticationPrincipal CustomUserDetails p){ return Map.of("count",notificationService.getUnreadCount(p.getUser().getId())); }
 @PatchMapping("/{id}/read") public NotificationResponse markRead(@PathVariable Long id,@AuthenticationPrincipal CustomUserDetails p){ return notificationService.markRead(id,p.getUser().getId()); }
 @PatchMapping("/read-all") public ResponseEntity<Void> markAllRead(@AuthenticationPrincipal CustomUserDetails p){ notificationService.markAllRead(p.getUser().getId()); return ResponseEntity.noContent().build(); }
}

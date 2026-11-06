package com.campusconnect.dto;
import com.campusconnect.entity.Notification;
import java.time.LocalDateTime;
public record NotificationResponse(Long id,String title,String message,String type,Long referenceId,boolean read,LocalDateTime createdAt) {
 public static NotificationResponse from(Notification n){ return new NotificationResponse(n.getId(),n.getTitle(),n.getMessage(),n.getType(),n.getReferenceId(),n.isRead(),n.getCreatedAt()); }
}

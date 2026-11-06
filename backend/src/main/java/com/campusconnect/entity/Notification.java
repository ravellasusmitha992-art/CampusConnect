package com.campusconnect.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity
@Table(name="notifications", indexes=@Index(name="idx_notification_recipient_created", columnList="recipient_id, created_at"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="recipient_id", nullable=false) private User recipient;
 @Column(nullable=false, length=140) private String title;
 @Column(nullable=false, length=1200) private String message;
 @Column(nullable=false, length=40) private String type;
 private Long referenceId;
 @Builder.Default @Column(nullable=false) private boolean read=false;
 @Column(nullable=false, updatable=false) private LocalDateTime createdAt;
 @PrePersist void onCreate(){ if(createdAt==null) createdAt=LocalDateTime.now(); }
}

package com.campusconnect.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class EventRequest {

    @NotBlank
    private String title;

    private String description;
    private String venue;
    private String posterUrl;
    private LocalDateTime eventDate;
}

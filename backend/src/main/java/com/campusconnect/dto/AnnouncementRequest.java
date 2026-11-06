package com.campusconnect.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AnnouncementRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private String posterUrl;
}

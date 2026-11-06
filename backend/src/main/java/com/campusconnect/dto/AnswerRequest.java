package com.campusconnect.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AnswerRequest {

    @NotBlank
    private String content;
}

package com.campusconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AnswerRequest {

    @NotBlank(message = "Answer content is required")
    @Size(max = 3000, message = "Answer must not exceed 3000 characters")
    private String content;
}

package com.campusconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class QuestionRequest {

    @NotBlank(message = "Question title is required")
    @Size(max = 150, message = "Question title must not exceed 150 characters")
    private String title;

    @NotBlank(message = "Question details are required")
    @Size(max = 3000, message = "Question details must not exceed 3000 characters")
    private String content;
}

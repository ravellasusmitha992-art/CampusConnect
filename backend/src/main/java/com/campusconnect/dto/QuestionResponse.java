package com.campusconnect.dto;

import java.time.LocalDateTime;
import java.util.List;

public record QuestionResponse(
        Long id,
        String title,
        String content,
        Long askedById,
        String askedByName,
        LocalDateTime createdAt,
        List<AnswerResponse> answers
) {}

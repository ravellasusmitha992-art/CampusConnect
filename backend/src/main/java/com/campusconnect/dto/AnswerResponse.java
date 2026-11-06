package com.campusconnect.dto;

import java.time.LocalDateTime;

public record AnswerResponse(
        Long id,
        String content,
        Long answeredById,
        String answeredByName,
        LocalDateTime createdAt
) {}

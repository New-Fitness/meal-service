package org.tesinitsyn.mealservice.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record MealDto(
        UUID id,
        UUID userId,
        String name,
        String description,
        Integer calories,
        LocalDateTime createdAt
) {}

package org.tesinitsyn.mealservice.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Информация о приёме пищи пользователя")
public record MealDto(

        @Schema(
                description = "Уникальный идентификатор приёма пищи",
                example = "6b83aaf9-6f9c-4b3d-9e8b-3aef55c4ccba"
        )
        UUID id,

        @Schema(
                description = "Идентификатор пользователя",
                example = "c71a9b23-7e41-4e6f-95b0-12cd6ad17aab"
        )
        UUID userId,

        @Schema(
                description = "Название блюда",
                example = "Овсянка с бананом и мёдом"
        )
        String name,

        @Schema(
                description = "Описание ингредиентов или состава блюда",
                example = "Овсяные хлопья, банан, мёд, молоко"
        )
        String description,

        @Schema(
                description = "Общая калорийность блюда (ккал)",
                example = "350"
        )
        Integer calories,

        @Schema(
                description = "Дата и время добавления приёма пищи",
                example = "2025-10-14T10:15:30"
        )
        LocalDateTime createdAt
) {
}

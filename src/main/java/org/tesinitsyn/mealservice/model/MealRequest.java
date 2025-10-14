package org.tesinitsyn.mealservice.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос на добавление нового приёма пищи для анализа")
public record MealRequest(

        @Schema(
                description = "Название блюда",
                example = "Овсянка с бананом и мёдом"
        )
        String name,

        @Schema(
                description = "Описание ингредиентов или состава блюда",
                example = "Овсяные хлопья, банан, мёд, молоко"
        )
        String description
) {
}

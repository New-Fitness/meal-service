package org.tesinitsyn.mealservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.mlgym.transfer.contract.meal.MealDto;
import org.mlgym.transfer.contract.meal.MealRequest;
import org.springframework.web.bind.annotation.*;
import org.tesinitsyn.mealservice.service.MealService;

import java.util.UUID;

@RestController
@RequestMapping("/api/meals")
@Tag(name = "Meals", description = "Операции с приёмами пищи и анализом калорийности")
public class MealController {

    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }


    @Operation(
            summary = "Добавить приём пищи пользователя",
            description = """
            Анализирует состав блюда (ингредиенты, калорийность, белки/жиры/углеводы)
            и сохраняет результат в историю пользователя.
            """,
            tags = {"Meals"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Приём пищи успешно проанализирован и сохранён",
                    content = @Content(schema = @Schema(implementation = MealDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные данные запроса",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Ошибка анализа или сохранения приёма пищи",
                    content = @Content
            )
    })
    @PostMapping("/{userId}")
    public MealDto addMeal(
            @Parameter(description = "Идентификатор пользователя", example = "c71a9b23-7e41-4e6f-95b0-12cd6ad17aab")
            @PathVariable UUID userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Описание блюда для анализа",
                    required = true,
                    content = @Content(schema = @Schema(implementation = MealRequest.class))
            )
            @RequestBody MealRequest request) {
        return mealService.analyzeAndSaveMeal(userId, request);
    }
}

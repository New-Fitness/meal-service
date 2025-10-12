package org.tesinitsyn.mealservice;

import org.springframework.web.bind.annotation.*;
import org.tesinitsyn.mealservice.model.MealDto;
import org.tesinitsyn.mealservice.model.MealRequest;
import org.tesinitsyn.mealservice.service.MealService;

import java.util.UUID;

@RestController
@RequestMapping("/api/meals")
public class MealController {

    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    @PostMapping("/{userId}")
    public MealDto addMeal(@PathVariable UUID userId, @RequestBody MealRequest request) {
        return mealService.analyzeAndSaveMeal(userId, request);
    }
}

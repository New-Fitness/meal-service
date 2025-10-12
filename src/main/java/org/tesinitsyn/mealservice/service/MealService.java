package org.tesinitsyn.mealservice.service;



import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.stereotype.Service;
import org.tesinitsyn.mealservice.model.MealDto;
import org.tesinitsyn.mealservice.model.MealRequest;
import org.tesinitsyn.mealservice.repository.MealRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class MealService {

    private final MealRepository repository;
    private final OllamaChatModel chatModel;

    public MealService(MealRepository repository, OllamaChatModel chatModel) {
        this.repository = repository;
        this.chatModel = chatModel;
    }

    public MealDto analyzeAndSaveMeal(UUID userId, MealRequest request) {

        ChatResponse aiResponse = chatModel.call(
                new Prompt(
                        "Count how many calories in here and just give me one number of calories there is no need of descriptive answer" + request.description(),
                        OllamaOptions.builder()
                                .model("llama3:latest")
                                .temperature(0.4)
                                .build()
                ));

        Integer calories = extractCalories(Objects.requireNonNull(aiResponse.getResult().getOutput().getText()));

        MealDto meal = new MealDto(
                UUID.randomUUID(),
                userId,
                request.name(),
                request.description(),
                calories,
                null
        );

        return repository.save(meal);
    }

    private Integer extractCalories(String text) {
        System.out.println("AI raw output:\n" + text);

        // Находим все числа в тексте
        var matcher = Pattern.compile("\\d+").matcher(text);
        List<Integer> numbers = new ArrayList<>();

        while (matcher.find()) {
            numbers.add(Integer.parseInt(matcher.group()));
        }

        if (numbers.isEmpty()) return null;

        // Если есть диапазон — берём среднее
        if (numbers.size() >= 2) {
            int avg = (numbers.get(0) + numbers.get(1)) / 2;
            System.out.println("🧮 Detected calorie range: " + numbers.get(0) + "-" + numbers.get(1) + " → avg=" + avg);
            return avg;
        }

        // Иначе — просто первое значение
        return numbers.get(0);
    }

}

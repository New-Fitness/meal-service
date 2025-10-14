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

/**
 * Сервис для анализа блюд с помощью AI-модели и сохранения информации о приёмах пищи в базе данных.
 * <p>
 * Использует {@link OllamaChatModel} для вычисления примерной калорийности блюда по его описанию.
 * После анализа результат сохраняется в базе через {@link MealRepository}.
 * </p>
 *
 * <h2>Пример использования</h2>
 * <pre>{@code
 * MealRequest request = new MealRequest("Овсянка с бананом", "Овсяные хлопья, банан, мёд");
 * MealDto meal = mealService.analyzeAndSaveMeal(userId, request);
 * System.out.println(meal.calories()); // например: 350
 * }</pre>
 *
 * @author tesinitsyn
 * @see MealRepository
 * @see MealDto
 * @see MealRequest
 */
@Service
public class MealService {

    private final MealRepository repository;
    private final OllamaChatModel chatModel;

    /**
     * Конструктор сервиса.
     *
     * @param repository репозиторий для сохранения данных о приёмах пищи
     * @param chatModel  AI-модель (Ollama), используемая для анализа калорийности
     */
    public MealService(MealRepository repository, OllamaChatModel chatModel) {
        this.repository = repository;
        this.chatModel = chatModel;
    }

    /**
     * Анализирует блюдо с помощью AI-модели и сохраняет результат в базу данных.
     * <p>
     * Отправляет запрос в {@link OllamaChatModel}, чтобы получить численную оценку калорийности
     * на основе текстового описания блюда. Извлекает число из ответа модели и сохраняет
     * результат в виде {@link MealDto}.
     * </p>
     *
     * @param userId  идентификатор пользователя, добавляющего приём пищи
     * @param request данные о блюде ({@link MealRequest}), включая название и описание
     * @return объект {@link MealDto} с рассчитанной калорийностью и сохранённый в базе
     */
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

    /**
     * Извлекает числовое значение калорий из текстового ответа AI-модели.
     * <ul>
     *     <li>Если найден диапазон (например, "300–400 ккал") — берёт среднее значение.</li>
     *     <li>Если найдено одно число — возвращает его напрямую.</li>
     *     <li>Если чисел нет — возвращает {@code null}.</li>
     * </ul>
     *
     * @param text текстовый ответ AI-модели
     * @return количество калорий или {@code null}, если данные не найдены
     */
    private Integer extractCalories(String text) {
        System.out.println("AI raw output:\n" + text);

        var matcher = Pattern.compile("\\d+").matcher(text);
        List<Integer> numbers = new ArrayList<>();

        while (matcher.find()) {
            numbers.add(Integer.parseInt(matcher.group()));
        }

        if (numbers.isEmpty()) return null;

        if (numbers.size() >= 2) {
            int avg = (numbers.get(0) + numbers.get(1)) / 2;
            System.out.println("🧮 Detected calorie range: " + numbers.get(0) + "-" + numbers.get(1) + " → avg=" + avg);
            return avg;
        }

        return numbers.get(0);
    }

}

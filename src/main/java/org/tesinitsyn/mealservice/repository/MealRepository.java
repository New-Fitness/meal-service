package org.tesinitsyn.mealservice.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.tesinitsyn.mealservice.model.MealDto;

import java.util.List;
import java.util.UUID;

import static com.fitnessai.meal.jooq.generated.Tables.MEAL;

/**
 * Репозиторий для работы с таблицей {@code meal} в базе данных.
 * <p>
 * Использует JOOQ DSLContext для безопасного и типизированного построения SQL-запросов.
 * Предоставляет методы для сохранения приёмов пищи и получения их по идентификатору пользователя.
 * </p>
 *
 * <h2>Пример использования</h2>
 * <pre>{@code
 * MealDto meal = new MealDto(...);
 * mealRepository.save(meal);
 *
 * List<MealDto> userMeals = mealRepository.findAllByUserId(userId);
 * }</pre>
 *
 * @author tesinitsyn
 * @see org.jooq.DSLContext
 * @see org.tesinitsyn.mealservice.model.MealDto
 */
@Repository
public class MealRepository {

    private final DSLContext dsl;

    /**
     * Конструктор репозитория.
     *
     * @param dsl экземпляр {@link DSLContext}, предоставляемый Spring через DI-контейнер.
     */
    public MealRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    /**
     * Сохраняет информацию о приёме пищи в таблицу {@code meal}.
     *
     * @param meal объект {@link MealDto}, содержащий данные о приёме пищи (пользователь, описание, калорийность и т.д.)
     * @return сохранённый объект {@link MealDto}
     */
    public MealDto save(MealDto meal) {
        dsl.insertInto(MEAL)
                .set(MEAL.ID, meal.id())
                .set(MEAL.USER_ID, meal.userId())
                .set(MEAL.NAME, meal.name())
                .set(MEAL.DESCRIPTION, meal.description())
                .set(MEAL.TOTAL_CALORIES, meal.calories())
                .execute();
        return meal;
    }

    /**
     * Возвращает список всех приёмов пищи, связанных с указанным пользователем.
     *
     * @param userId уникальный идентификатор пользователя ({@link UUID})
     * @return список {@link MealDto} — приёмы пищи пользователя, отсортированные по дате создания
     */
    public List<MealDto> findAllByUserId(UUID userId) {
        return dsl.selectFrom(MEAL)
                .where(MEAL.USER_ID.eq(userId))
                .fetch(record -> new MealDto(
                        record.getId(),
                        record.getUserId(),
                        record.getName(),
                        record.getDescription(),
                        record.getTotalCalories(),
                        record.getCreatedAt()
                ));
    }
}

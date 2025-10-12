package org.tesinitsyn.mealservice.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.tesinitsyn.mealservice.model.MealDto;

import java.util.List;
import java.util.UUID;

import static com.fitnessai.meal.jooq.generated.Tables.MEAL;

@Repository
public class MealRepository {

    private final DSLContext dsl;

    public MealRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public MealDto save(MealDto meal) {
        dsl.insertInto(MEAL)
                .set(MEAL.ID, meal.id())
                .set(MEAL.USER_ID, meal.userId())
                .set(MEAL.NAME, meal.name())
                .set(MEAL.DESCRIPTION, meal.description())
                .set(MEAL.CALORIES, meal.calories())
                .execute();
        return meal;
    }

    public List<MealDto> findAllByUserId(UUID userId) {
        return dsl.selectFrom(MEAL)
                .where(MEAL.USER_ID.eq(userId))
                .fetch(record -> new MealDto(
                        record.getId(),
                        record.getUserId(),
                        record.getName(),
                        record.getDescription(),
                        record.getCalories(),
                        record.getCreatedAt()
                ));
    }
}

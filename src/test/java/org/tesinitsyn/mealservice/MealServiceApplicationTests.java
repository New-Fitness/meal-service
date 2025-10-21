package org.tesinitsyn.mealservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.tesinitsyn.mealservice.ai.service.ChatMemoryService;

@SpringBootTest
@ActiveProfiles("test")
@MockBean(ChatMemoryService.class) // пример: замокаем бин, который ходит в Ollama
class MealServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}

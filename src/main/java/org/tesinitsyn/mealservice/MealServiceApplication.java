package org.tesinitsyn.mealservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MealServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MealServiceApplication.class, args);
    }

//    @Bean
//    CommandLineRunner testConfig(@Value("${spring.ai.ollama.model}") String model) {
//        return args -> System.out.println("⚙️ Active Ollama model: " + model);
//    }

}

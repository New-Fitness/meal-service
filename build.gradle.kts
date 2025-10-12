import org.jooq.meta.jaxb.Logging

plugins {
    java
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
    id("nu.studer.jooq") version "8.2"
    id("org.liquibase.gradle") version "2.2.0"
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
}

group = "org.tesinitsyn"
version = "0.0.1-SNAPSHOT"
description = "meal-service"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

val dbUrl = "jdbc:postgresql://localhost:5432/fitness_ai"
val dbUser = "postgres"
val dbPassword = "password"

extra["springAiVersion"] = "1.0.3"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.ai:spring-ai-starter-model-ollama")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.postgresql:postgresql:42.7.3")
    jooqGenerator("org.postgresql:postgresql:42.7.3")
    implementation("org.liquibase:liquibase-core")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // 💡 Важно: runtime для Liquibase
    liquibaseRuntime("org.liquibase:liquibase-core")
    liquibaseRuntime("org.postgresql:postgresql:42.7.3")
    liquibaseRuntime("info.picocli:picocli:4.7.5") // CLI-зависимость, нужна плагину
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.ai:spring-ai-bom:${property("springAiVersion")}")
    }
}

// ============================
// 🚀 Liquibase
// ============================
liquibase {
    activities.register("main") {
        arguments = mapOf(
            "changeLogFile" to "src/main/resources/db/changelog/db.changelog-master.yaml",
            "url" to dbUrl,
            "username" to dbUser,
            "password" to dbPassword
        )
    }
    runList = "main"
}

// --- jOOQ code generation ---
jooq {
    version.set("3.19.9")
    configurations {
        create("main") {
            jooqConfiguration.apply {
                logging = Logging.WARN
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = dbUrl
                    user = dbUser
                    password = dbPassword
                }
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                        excludes = "databasechangelog|databasechangeloglock"
                    }
                    generate.apply {
                        isDaos = true
                        isPojos = true
                        isRecords = true
                    }
                    target.apply {
                        packageName = "com.fitnessai.meal.jooq.generated"
                        directory = "build/generated-jooq"
                    }
                }
            }
        }
    }
}

tasks.named("generateJooq") {
    dependsOn("update")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

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
    toolchain { languageVersion = JavaLanguageVersion.of(21) }
}

configurations {
    compileOnly { extendsFrom(annotationProcessor.get()) }
}

repositories { mavenCentral() }

extra["springAiVersion"] = "1.0.3"

// ============================
// 🌿 ENV: окружение (dev/test/prod)
// ============================

val isTestTask = gradle.startParameter.taskNames.any { it.contains("test", ignoreCase = true) }
val env = project.findProperty("env")
    ?: if (isTestTask) "test"
    else (System.getenv("SPRING_PROFILES_ACTIVE") ?: "dev")

val dbConfig = when (env) {
    "prod" -> mapOf(
        "url" to "jdbc:postgresql://localhost:5432/fitness_ai",
        "user" to "postgres",
        "password" to (System.getenv("DB_PASSWORD") ?: "password")
    )
    "test" -> mapOf(
        "url" to "jdbc:postgresql://localhost:5432/fitness_ai",
        "user" to "postgres",
        "password" to "password"
    )
    else -> mapOf(
        "url" to "jdbc:postgresql://localhost:5432/fitness_ai",
        "user" to "postgres",
        "password" to "password"
    )
}

val dbUrl = dbConfig["url"]!!
val dbUser = dbConfig["user"]!!
val dbPassword = dbConfig["password"]!!

println("▶️  Active environment: $env")
println("📦  Using DB: $dbUrl")

// ============================
// 📦 Dependencies
// ============================

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.liquibase:liquibase-core")
    implementation("org.springframework.ai:spring-ai-starter-model-ollama")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    implementation("org.postgresql:postgresql:42.7.3")
    jooqGenerator("org.postgresql:postgresql:42.7.3")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Runtime для Liquibase
    liquibaseRuntime("org.liquibase:liquibase-core")
    liquibaseRuntime("org.postgresql:postgresql:42.7.3")
    liquibaseRuntime("info.picocli:picocli:4.7.5")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.ai:spring-ai-bom:${property("springAiVersion")}")
    }
}

// ============================
// 🧱 Liquibase
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

// ============================
// 🧹 Безопасная очистка и миграция
// ============================

tasks.register("liquibaseCleanAndUpdate") {
    group = "database"
    description = "Drops and reapplies all Liquibase migrations (safe for local use)"
    doLast {
        val liquibaseTasks = listOf("liquibaseDropAll", "liquibaseUpdate")
        liquibaseTasks.forEach { name ->
            val task = tasks.findByName(name)
            if (task != null) {
                println("▶️  Running $name ...")
                task.actions.forEach { it.execute(task) }
            } else {
                println("⚠️  Task $name not found — skipping (normal for CI).")
            }
        }
    }
}

// ============================
// 🧬 jOOQ code generation
// ============================

jooq {
    version.set("3.19.9")
    configurations {
        create("main") {
            generateSchemaSourceOnCompilation.set(false)
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

// ============================
// 🧩 Безопасная связка Liquibase → jOOQ
// ============================

gradle.projectsEvaluated {
    val liquibaseUpdate = tasks.findByName("liquibaseUpdate")
    val generateJooq = tasks.findByName("generateJooq")
    if (liquibaseUpdate != null && generateJooq != null) {
        generateJooq.dependsOn(liquibaseUpdate)
        println("✅ Linked liquibaseUpdate → generateJooq")
    } else {
        println("⚠️  liquibaseUpdate or generateJooq not found at configuration time — skipping link.")
    }
}

// ============================
// 🧪 Testing
// ============================

tasks.withType<Test> {
    useJUnitPlatform()
    systemProperty("spring.profiles.active", "test")
    project.extensions.extraProperties["env"] = "test"
}

// ============================
// 🧰 Удобные ярлыки для локалки
// ============================

tasks.register("dbResetAndGenerate") {
    group = "local-dev"
    description = "Reset DB, apply migrations, generate jOOQ code"
    dependsOn("liquibaseCleanAndUpdate", "generateJooq")
}

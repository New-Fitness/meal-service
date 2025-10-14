import org.gradle.kotlin.dsl.support.serviceOf
import org.jooq.meta.jaxb.Logging

plugins {
    java
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
    id("nu.studer.jooq") version "8.2"
    id("org.liquibase.gradle") version "2.2.0"
    id("org.springdoc.openapi-gradle-plugin") version "1.8.0"
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
}

group = "org.tesinitsyn"
version = "0.0.1-SNAPSHOT"
description = "meal-service"

java {
    toolchain { languageVersion = JavaLanguageVersion.of(21) }
}

repositories { mavenCentral() }

extra["springAiVersion"] = "1.0.3"

// ============================
// 🌿 ENVIRONMENT
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
// 📦 DEPENDENCIES
// ============================

dependencies {
    implementation("org.liquibase:liquibase-core:4.29.2")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.springframework.ai:spring-ai-starter-model-ollama")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    implementation("org.postgresql:postgresql:42.7.3")
    jooqGenerator("org.postgresql:postgresql:42.7.3")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    liquibaseRuntime("org.liquibase:liquibase-core:4.29.2")
    liquibaseRuntime("org.postgresql:postgresql:42.7.3")
    liquibaseRuntime("info.picocli:picocli:4.7.5")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.ai:spring-ai-bom:${property("springAiVersion")}")
    }
}

// ============================
// 🧱 LIQUIBASE CONFIG
// ============================

liquibase {
    activities.register("main") {
        arguments = mapOf(
            "changelogFile" to "src/main/resources/db/changelog/db.changelog-master.yaml",
            "url" to dbUrl,
            "username" to dbUser,
            "password" to dbPassword
        )
    }
    runList = "main"
}

openApi {
    apiDocsUrl.set("http://localhost:8081/v3/api-docs.yaml")
    outputDir.set(file("$projectDir/docs/openapi"))
    outputFileName.set("openapi.yaml")
}

// ============================
// 🧬 jOOQ CODEGEN
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
// ✅ TASK ORDER FIX
// ============================

tasks.named("generateJooq") {
    dependsOn("update") // это задача из Liquibase Gradle Plugin
}

tasks.named("compileJava") {
    dependsOn("generateJooq")
}


// ============================
// 🧪 TESTS
// ============================

tasks.withType<Test> {
    useJUnitPlatform()
    systemProperty("spring.profiles.active", "test")
    project.extensions.extraProperties["env"] = "test"
}

// ============================
// 🧰 LOCAL SHORTCUT
// ============================

tasks.register("dbResetAndGenerate") {
    group = "local-dev"
    description = "Reset DB, apply migrations, generate jOOQ code"
    doLast {
        println("🧹 Resetting DB...")
        val execOps = project.serviceOf<ExecOperations>()
        execOps.exec { commandLine("bash", "-c", "./gradlew liquibaseDropAll || true") }
        execOps.exec { commandLine("bash", "-c", "./gradlew liquibaseUpdate") }
        execOps.exec { commandLine("bash", "-c", "./gradlew generateJooq") }
    }
}

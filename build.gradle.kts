import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // id("org.springframework.boot") version "2.7.14"
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"

    application
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
    kotlin("plugin.jpa") version "1.9.22"
    kotlin("plugin.noarg") version "1.9.22"
    kotlin("kapt") version "1.9.22"
}

group = "kr.xit"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

application {
    mainClass = "kr.xit.KrXitApplication"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")

    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
    kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")

    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

    // p6spy ----------------------------------------------------------------
    implementation("p6spy:p6spy:3.9.1")
    // p6spy : sprin boot 2.X
    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.8.1")
    // p6spy : sprin boot 3.X
    // implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.1")
    // -----------------------------------------------------------------------
    runtimeOnly("org.springframework.boot:spring-boot-devtools")

    // zxing qrcode
    implementation("com.google.zxing:javase:3.5.3")
    implementation("com.google.zxing:core:3.5.3")

    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.kotest:kotest-runner-junit5:5.6.2")
    testImplementation("io.kotest:kotest-assertions-core:5.6.2")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")
    // mockk
    testImplementation("io.mockk:mockk:1.13.8")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        // freeCompilerArgs += "-Xjsr305=strict"
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// jar 생성시 plain jar와 일반 jar가 생성
// plain jar를 생성하지 않도록 한다
tasks.named<Jar>("jar") {
    enabled = false
}

plugins {
    id("java")
}

group = "com.github.msemitkin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter:3.3.0")
    implementation("org.telegram:telegrambots-springboot-longpolling-starter:7.2.1")
    implementation("org.telegram:telegrambots-client:7.2.1")

    implementation("com.google.cloud:google-cloud-language:2.45.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

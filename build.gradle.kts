val exposed_version: String by project
val h2_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val postgresql_version: String by project
val hikaricp_version: String by project
val rssreader_version: String by project
val task_scheduling_version: String by project

plugins {
    kotlin("jvm") version "2.2.20"
    id("io.ktor.plugin") version "3.3.1"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.20"
}

group = "com.simuel"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor Core
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-config-yaml")
    
    // Serialization
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-content-negotiation")
    
    // CORS
    implementation("io.ktor:ktor-server-cors")
    
    // Logging
    implementation("io.ktor:ktor-server-call-logging")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    
    // Database - Exposed
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposed_version")
    
    // Kotlinx DateTime
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")
    
    // PostgreSQL Driver
    implementation("org.postgresql:postgresql:$postgresql_version")
    
    // Connection Pool
    implementation("com.zaxxer:HikariCP:$hikaricp_version")
    
    // H2 for testing
    implementation("com.h2database:h2:$h2_version")
    
    // RSS Reader
    implementation("com.apptasticsoftware:rssreader:$rssreader_version")
    
    // HTTP Client for URL validation
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-client-content-negotiation")

    // Environment Variables
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

    // Testing
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

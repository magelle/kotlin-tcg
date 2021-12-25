import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    application
}

group = "magelle"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("io.arrow-kt:arrow-stack:1.0.1"))

    implementation("io.arrow-kt:arrow-core")
    implementation("io.arrow-kt:arrow-optics")

    implementation( "com.h2database:h2:1.4.200")
    implementation("org.jetbrains.exposed:exposed-core:0.36.2")
    implementation("org.jetbrains.exposed:exposed-dao:0.36.2")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.36.2")

    testImplementation(kotlin("test"))
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.25")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}
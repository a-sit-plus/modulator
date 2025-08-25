plugins {
    `kotlin-dsl`
    `maven-publish`
}

group = "at.asitplus"
version = "0.1-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
    google()
}

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.0")
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
}

gradlePlugin {
    plugins.register("gradle-modulator") {
        id = "$group.gradle.modulator"
        implementationClass = "at.asitplus.gradle.modulator.Plugin"
    }
}
kotlin {
    jvmToolchain(17)
}
publishing {
    repositories {
        mavenLocal()
    }
}
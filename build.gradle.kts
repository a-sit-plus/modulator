plugins {
    `kotlin-dsl`
    signing
    id("com.gradle.plugin-publish") version "1.2.1"
}

group = "at.asitplus"
version = "0.1.0"

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
    website = "https://a-sit-plus.github.io/modulator"
    vcsUrl = "https://github.com/a-sit-plus/modulator"
    plugins.register("modulator") {
        id = "$group.gradle.modulator"
        displayName = "modulator Gradle Plugin"
        implementationClass = "at.asitplus.gradle.modulator.Plugin"
        description = "KMP compileOnly Dependencies without Dependency Hell"
        tags = listOf("kotlin", "kmp", "multiplatform", "dependencyManagement")
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
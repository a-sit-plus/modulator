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


}

gradlePlugin {
    plugins.register("modulator") {
        id = "$group.modulator"
        implementationClass = "at.asitplus.gradle.modulator.Plugin"
    }
}
kotlin {
    jvmToolchain(17)
}
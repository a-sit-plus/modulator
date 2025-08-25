import at.asitplus.gradle.modulator.carrier

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    `maven-publish`
    id("at.asitplus.gradle.modulator") version "0.1-SNAPSHOT"
}
group ="at.asitplus"
version ="0.1-SNAPSHOT"
kotlin {
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Mapper"
            isStatic = true
        }
    }

    jvm()

    sourceSets {
        commonMain.dependencies {
           carrier(project(":cose"))
           carrier(project(":jose"))
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}


publishing {
    repositories {
        mavenLocal()
    }
}

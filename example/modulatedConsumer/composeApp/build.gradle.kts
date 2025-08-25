plugins {
    alias(libs.plugins.kotlinMultiplatform)
    id("at.asitplus.gradle.modulator") version "0.1-SNAPSHOT"
}

kotlin {
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm()

    sourceSets {
        commonMain.dependencies {
            api("at.asitplus:cose:0.1-SNAPSHOT")
            api("at.asitplus:jose:0.1-SNAPSHOT")

        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {

        }
    }
}





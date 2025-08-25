plugins {
    alias(libs.plugins.kotlinMultiplatform)
    `maven-publish`
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
            baseName = "Jose"
            isStatic = true
        }
    }

    jvm()

    sourceSets {
        commonMain.dependencies {
            // put your Multiplatform dependencies here
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

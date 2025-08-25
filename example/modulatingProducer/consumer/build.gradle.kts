import at.asitplus.gradle.modulator.carrier

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    `maven-publish`
    id("at.asitplus.gradle.modulator") version "0.1-SNAPSHOT"
}
group ="at.asitplus"
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
           api("at.asitplus:cose:unspecified")
           api("at.asitplus:jose:unspecified")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
afterEvaluate {

    configurations.matching { it.isCanBeResolved }.configureEach {
        attributes.attributes.keySet().forEach { attribute ->
            println("FOUND ATTRIBUTE: ${attribute.name}")
        }
    }
}

publishing {
    repositories {
        mavenLocal()
    }
}

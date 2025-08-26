//import org.gradle.kotlin.dsl.support.listFilesOrdered

plugins {
    `kotlin-dsl`
    signing
    id("com.gradle.plugin-publish") version "1.2.1"
//    id("org.jetbrains.dokka") version "2.0.0"
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
    website = "https://github.com/a-sit-plus/modulator"
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
        mavenLocal {
            signing.isRequired = false
        }
    }
}
signing {
    val signingKeyId: String? by project
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
    sign(publishing.publications)
}
/*
val dokkaOutputDir = "$projectDir/docs"
dokka {
    val moduleDesc = File("$rootDir/dokka-tmp.md").also { it.createNewFile() }
    val readme =
        File("${rootDir}/README.md").readText()
    moduleDesc.writeText("Module \n\n$readme")
    moduleName.set("")
    basePublicationsDirectory.set(file("${rootDir}/docs"))

    dokkaSourceSets.main {
        includes.from(moduleDesc)
        sourceLink {
            val path = "${projectDir}/src/$name/kotlin"
            println(path)
            localDirectory.set(file(path))
            remoteUrl.set(
                uri("https://github.com/a-sit-plus/modulator/tree/main/src/$name/kotlin")
            )
            // Suffix which is used to append the line number to the URL. Use #L for GitHub
            remoteLineSuffix.set("#L")
        }
    }

    pluginsConfiguration.html {
        footerMessage = "&copy; 2025 A-SIT Plus GmbH"
    }
}

tasks.dokkaGenerate {
    doLast {
        rootDir.listFilesOrdered { it.extension.lowercase() == "png" || it.extension.lowercase() == "svg" }
            .forEach { it.copyTo(File("$rootDir/docs/html/${it.name}"), overwrite = true) }

    }
}


val deleteDokkaOutputDir by tasks.register<Delete>("deleteDokkaOutputDirectory") {
    delete(dokkaOutputDir)
}*/
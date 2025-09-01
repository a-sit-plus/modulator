<div align="center">

<img alt="modulator – compileOnly Kotlin Multiplatform Dependencies without Dependency Hell" src="modulator.png">

# modulator – `compileOnly` Kotlin Multiplatform Dependencies without Dependency Hell

[![A-SIT Plus Official](https://raw.githubusercontent.com/a-sit-plus/a-sit-plus.github.io/709e802b3e00cb57916cbb254ca5e1a5756ad2a8/A-SIT%20Plus_%20official_opt.svg)](https://plus.a-sit.at/open-source.html)
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-brightgreen.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)
[![Gradle plugin portal](https://img.shields.io/gradle-plugin-portal/v/at.asitplus.gradle.modulator?label=Gradle%20plugin%20portal)](https://plugins.gradle.org/plugin/at.asitplus.gradle.modulator)


**_modulator_ – a lean Gradle plugin that gives Kotlin Multiplatform the superpower of JVM-style `compileOnly` dependencies! No bloat, no dirty tricks, just clean, modular APIs, and full toolchain compatibility – forever!**


</div>

Imagine a Spring Boot core with optional persistence: JPA/Hibernate or MongoDB. You want expressive extension functions
like `Order.toJpaEntity()` or `Order.toMongoDocument()` to become available automatically when the corresponding starter
is on the classpath, without forcing every service to depend on both stacks.  
On the JVM you’d create these adapters using `compileOnly` dependencies on JPA/Hibernate and MongoDB to keep your core clean.
Unfortunately, _Kotlin Multiplatform says no!_  
Hence, optional, extension‑driven integrations either bloat dependency graphs or require tedious manual wiring.


Enter _modulator_ – a lean Gradle plugin that brings two complementary capabilities to Kotlin Multiplatform:

1. Piggyback modules with extension functionality and/or glue code on two or more `carrier` modules within a multi-module project.
2. Automatically add those piggybacked modules as dependencies when all of their carriers are present in a consuming project.


Just apply `at.asitplus.gradle.modulator` to any Gradle module that requires either capability.
That’s it – no custom wiring, no dependency clutter, no hacks, no compiler plugins, no code generation,
but full backwards compatibility with all Kotlin and Gradle tooling!


## Quickstart
_modulator_ introduces a new type of dependency: `carrier` dependencies, that are available alongside `api`, `implementation`, and so forth.
A bridge / glue module depends on two or more carrier modules (within the same multi-module gradle project).
When all carriers are present in a consumer, the bridge module is automatically pulled in.


### Creating Bridge Modules ("Modulation")
If `bridgeModule` should provide glue functionality between `modA` and `modB`
* apply the `at.asitplus.gradle.modulator` Gradle plugin
* add `modA` and `modB` as `carrier` dependencies:

```kotlin
//build.gradle.kts of bridgeModule

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    `maven-publish`
    /* …… */
    id("at.asitplus.gradle.modulator") version "$modulatorVersion"
}

kotlin {
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "BridgeModule"
            isStatic = true
        }
    }

    jvm()

    // add additional targets as desired
    
    sourceSets {
        //does not have to be commonMain, but it makes the most sense
        commonMain.dependencies {
           carrier(project(":modA")) //no need to modify modA's buildscript
           carrier(project(":modB")) //no need to modify modB's buildscript
        }
    }
}

//…… publishing, etc.
```

This will add metadata to both `modA` and `modB` publications, such that the published artifacts of both contain the information that
`bridgeModule` should be pulled in when both `modA` and `modB` are added as dependencies to a consuming project.  
The buildscripts of neither `modA` nor `modB` require any changes or even the modulator gradle plugin.

### Automagically Pulling in Bridge Modules ("Demodulation")

_modulator_ works its magic in consuming projects even less obtrusively:
Just apply the modulator Gradle plugin in consumers and the carrier dependencies as regular `api` or `implementation` dependencies.
No other changes are required to the buildscript.

```kotlin
//build.gradle.kts of consuming project
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    id("at.asitplus.gradle.modulator") version "$modulatorVersion"
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
    
    // add additional targets as desired
    
    sourceSets {
        commonMain.dependencies {
            api("com.example.modA:$modAversion")
            api("com.example.modB:$modBversion")
            //that's it! bridgeModule will be automagically pulled in
        }
    }
}
```

If `modA` and `modB` are added as `api` dependencies, the bridge module will also be added as `api` dependency. The same holds
for `implementation` dependencies.

For library authors, this is not quite as hassle-free as `compileOnly` dependencies on the JVM but:
* The project setup remains fully transparent, predictable, intelligible and easily maintainable.
* The use of dedicated bridge modules and enriched Gradle metadata on carrier modules is fully and perfectly backwards-compatible with the whole Gradle/KMP ecosystem, and it will stay that way.
* Your project either compiles or it does not run. No `RuntimeException` or other unpleasant surprises, because everything is known at compile-time.

In the end, no invasive changes to the KMP/Gradle tooling are required, as _modulator_ simply adds additional dependencies in the same way as adding them explicitly yourself.

## Concrete Example Projects
The `example` directory contains two projects that showcase _modulator_:
1. `modulatingProducer` contains three modules:
   1. `cose` providing a single sample COSE-ish data class
   2. `jose` providing a single sample JOSE-ish data class
   3. `coseToJose` providing mapper functionality from COSE to JOSE
2. `modulatedConsumer` contains a single module that adds `cose` and `jose` dependencies and uses the mapping functionality provided by `cosetoJose`, showcasing that no explicit adding of this dependency is needed

To try it out: publish `modulatingProduce` to maven local and open `modulatedConsumer` in IDEA to witness the magic!


<hr>

<p align="center">
The Apache License does not apply to the logos, (including the A-SIT logo) and the project/module name(s), as these are the sole property of
A-SIT/A-SIT Plus GmbH and may not be used in derivative works without explicit permission!
</p>

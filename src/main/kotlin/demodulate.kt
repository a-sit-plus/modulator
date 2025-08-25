package at.asitplus.gradle.modulator

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import java.util.concurrent.ConcurrentHashMap

internal fun Project.demodulate() {
    val modulationsBySourceSet =
        ConcurrentHashMap<KotlinSourceSet, ConcurrentHashMap<Configuration, ConcurrentHashMap<String, Modulation>>>()
    extensions.getByType<KotlinMultiplatformExtension>().apply {
        sourceSets.forEach { srcSet ->
            val perConfig = ConcurrentHashMap<Configuration, ConcurrentHashMap<String, Modulation>>()
            srcSet.project.configurations.forEach { cfg ->
                val modulations = ConcurrentHashMap<String, Modulation>()
                perConfig[cfg] = modulations
                cfg.dependencies.forEach { dep ->

                    val group = dep.group ?: return@forEach
                    val artifact = dep.name
                    val version = dep.version ?: return@forEach

                    val gav = "$group:$artifact:$version"

                    val probe = configurations.detachedConfiguration(dependencies.create("$gav@module")).apply {
                        isCanBeConsumed = false
                        isCanBeResolved = true
                        // Critical: don't pull the whole graph; we only need the root's metadata
                        isTransitive = false
                    }
                    val artifactView = probe.incoming.artifactView {
                        isLenient = true
                    }
                    val metadataFiles = artifactView.artifacts.artifactFiles.files


                    if (metadataFiles.isEmpty()) return@forEach


                    val f = metadataFiles.singleOrNull() ?: return@forEach
                    val text = f.readText()
                    Json.parseToJsonElement(text).let {
                        it.jsonObject.get("variants")?.jsonArray?.forEach {
                            val variant = it.jsonObject

                            if (variant.get("name")?.jsonPrimitive?.content == "metadataApiElements") {
                                val modulation = variant.get("attributes")?.jsonObject?.let { attributes ->
                                    attributes.get("$ATTR_PREFIX_CARRIER")?.jsonPrimitive?.content
                                }

                                modulation?.let {
                                    val mod = Modulation.load(it)
                                    modulations[gav] = mod
                                }
                            }
                        }
                    }
                }
                if (modulations.isNotEmpty()) {
                    modulationsBySourceSet[srcSet] = perConfig
                }
            }
        }
        modulationsBySourceSet.forEach { (srcSet, perConfig) ->
            perConfig.forEach { (cfg, modulations) ->
                runCatching {
                    val modulationDefs = modulations.values
                    val addedDeps = modulations.keys

                    val demodulated = modulationDefs.filter {
                        addedDeps.containsAll(it.carriers.map { carrier -> carrier.group + ":" + carrier.artifact + ":" + carrier.version.preferredVersion })
                    }.distinctBy { it.signal }.map { it.signal }
                    demodulated.forEach { dep ->
                        srcSet.dependencies {
                            val dependencyNotation = dep.group + ":" + dep.artifact + ":" + dep.version.preferredVersion
                            if (srcSet.apiConfigurationName == cfg.name) {
                                api(dependencyNotation)
                                logger.lifecycle(">> modulator added API  dependency $dependencyNotation to ${srcSet.name}")
                            } else if (srcSet.implementationConfigurationName == cfg.name) {
                                implementation(dependencyNotation)
                                logger.lifecycle(">> modulator added IMPL dependency $dependencyNotation to ${srcSet.name}")
                            }
                        }
                    }
                }.getOrElse {
                    logger.warn("Could not demodulate for ${srcSet.name}$, ${cfg.name}, ${modulations.entries.joinToString()}")
                }
            }
        }
    }
}
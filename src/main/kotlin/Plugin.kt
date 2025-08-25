package at.asitplus.gradle.modulator

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.extraProperties
import kotlin.random.Random

private val KEY_COLLECTED_CARRIERS = Random.nextBits(32).toString(36)
private val KEY_NUMOF_CARRIERS = Random.nextBits(32).toString(36)
internal val Project.collectedCarriers: MutableList<Project>
    @Suppress("UNCHECKED_CAST")
    get() = runCatching { extraProperties[KEY_COLLECTED_CARRIERS] as MutableList<Project> }.getOrElse {
        mutableListOf<Project>().also { extraProperties[KEY_COLLECTED_CARRIERS] = it }
    }

internal var Project.numberOfCarriers: Int
    get() = runCatching { extraProperties[KEY_NUMOF_CARRIERS] as Int }.getOrElse {
        0.also { extraProperties[KEY_COLLECTED_CARRIERS] = it }
    }
    set(value) {
        extraProperties[KEY_NUMOF_CARRIERS] = value
    }

class Plugin : org.gradle.api.Plugin<Project> {
    override fun apply(target: Project) {
        if (target == target.rootProject) return

        if (target.plugins.findPlugin("org.jetbrains.kotlin.multiplatform") == null) {
            return
        }
        target.afterEvaluate { demodulate() }
    }
}

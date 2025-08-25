package at.asitplus.gradle.modulator


import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency


// PUBLIC API
@Suppress("unused")
fun org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler.carrier(notation: Any) {
    val dep = notation.toProjectDependencyOrThrow()
    project.logger.lifecycle(">> modulator collecting carrier ${dep.group}:${dep.name}:${dep.version}")
    project.collectCarrier(dep)
    api(dep) // preserve original behavior
}


private fun Any.toProjectDependencyOrThrow(): ProjectDependency =
    when (this) {
        is ProjectDependency -> this
        else -> error(
            "carrier(...) only accepts project() dependencies, e.g. carrier(project(\":some:module\")). " +
                    "Received: $this"
        )
    }

/**
 * Ensures the producer project (the one being depended on) publishes an attribute
 * on its 'kotlinMetadata' configuration so it shows up in Gradle Module Metadata.
 *
 * Works regardless of configuration creation order.
 */
private fun Project.collectCarrier(dep: ProjectDependency) {
    numberOfCarriers++
    rootProject.gradle.projectsEvaluated {
        val producer = rootProject.project(dep.path)
        collectedCarriers.add(producer)
        if (collectedCarriers.size == numberOfCarriers) {
            addMetadataToCarriers()
        }
    }

}

internal fun Project.addMetadataToCarriers() {
    val carriers = collectedCarriers.map { it.toModuleDeclaration() }
    val modulation = Modulation(carriers, toModuleDeclaration())
    logger.lifecycle(">> modulator modulating ${modulation.signal.let { it.group + ":${it.artifact}" }} using carriers: ${carriers.joinToString(prefix = "[", postfix = "]") { it.group + ":" + it.artifact }}")
    //at this point we know we have a valid modulation
    collectedCarriers.forEach { module ->
        module.configurations.matching { it.name == "metadataApiElements" }.forEach { cfg ->
            cfg.attributes.attribute(ATTR_CARRIER, modulation.store())
        }
    }
}



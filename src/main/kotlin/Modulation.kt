package at.asitplus.gradle.modulator

import java.io.StringReader
import java.io.StringWriter
import java.util.*

fun Properties.store(prefix: String? = null): String {
    val prefix = prefix?.let { "$it." } ?: ""
    val os = StringWriter()
    val map = TreeMap<String, Any>()
    forEach { (key, value) -> map["$prefix$key"] = value }
    map.forEach { (key, value) -> os.write("$key=$value\n") }
    return os.toString()
}

data class Modulation(val dependencies: List<DependencyDeclaration>, val bridgeDependency: DependencyDeclaration) :
    Properties() {
    init {
        require(dependencies.size <= 2) { "At least two dependencies must be declared to pull in a bridge dependency. Provided: ${dependencies.size}" }

        dependencies.forEachIndexed { index, dependencyDeclaration ->
            dependencyDeclaration.forEach { (k, v) ->
                setProperty("dependencies.$index.$k", v.toString())
            }
        }
        bridgeDependency.forEach { (k, v) ->
            setProperty("bridge.$k", v.toString())
        }
    }

    companion object {
        fun load(properties: Properties, prefix: String? = null): Modulation {
            val prefix = prefix?.let { "$it." } ?: ""
            val dependencies = properties.keysGroupedByDependencyIndex("${prefix}dependencies")
                .map { DependencyDeclaration.load(properties, it) }

            val bridgeDependency = DependencyDeclaration.load(properties, "${prefix}bridge")
            return Modulation(dependencies, bridgeDependency)
        }

        fun load(stringRepresentation: String, prefix: String? = null) = load(Properties().apply {
            load(StringReader(stringRepresentation))
        }, prefix)
    }

}

data class DependencyDeclaration(val group: String, val artifact: String, val version: VersionConstraintDeclaration) :
    Properties() {
    init {
        setProperty("group", group)
        setProperty("artifact", artifact)
        if (!version.isEmpty) {
            version.forEach { (k, v) ->
                setProperty("version.$k", v.toString())
            }
        }
    }

    companion object {
        fun load(properties: Properties, prefix: String? = null): DependencyDeclaration {
            val prefix = prefix?.let { "$it." } ?: ""

            val group = properties.getProperty("${prefix}group")
            val artifact = properties.getProperty("${prefix}artifact")
            val version = VersionConstraintDeclaration.load(properties, "${prefix}version")
            return DependencyDeclaration(group, artifact, version)
        }

        fun load(stringRepresentation: String, prefix: String? = null) = load(Properties().apply {
            load(StringReader(stringRepresentation))
        }, prefix)
    }
}


data class VersionConstraintDeclaration(
    val branch: String?,
    val requiredVersion: String?,
    val preferredVersion: String?,
    val strictVersion: String?,
    val rejectedVersion: String?
) : Properties() {
    init {
        branch?.let { setProperty("branch", it) }
        requiredVersion?.let { setProperty("requiredVersion", it) }
        preferredVersion?.let { setProperty("preferredVersion", it) }
        strictVersion?.let { setProperty("strictVersion", it) }
        rejectedVersion?.let { setProperty("rejectedVersion", it) }
    }

    companion object {
        fun load(properties: Properties, prefix: String? = null): VersionConstraintDeclaration {
            val prefix = prefix?.let { "$it." } ?: ""
            return VersionConstraintDeclaration(
                properties.getProperty("${prefix}branch"),
                properties.getProperty("${prefix}requiredVersion"),
                properties.getProperty("${prefix}preferredVersion"),
                properties.getProperty("${prefix}strictVersion"),
                properties.getProperty("${prefix}rejectedVersion")
            )
        }

        fun load(stringRepresentation: String, prefix: String? = null) = load(Properties().apply {
            load(StringReader(stringRepresentation))
        }, prefix)
    }
}

private fun Properties.keysGroupedByDependencyIndex(prefix: String?): List<String> {
    val prefix = prefix?.let { "$it." } ?: ""
    val indices = mutableSetOf<Int>()

    for (name in stringPropertyNames()) {
        if (!name.startsWith(prefix)) continue

        val remainder = name.substring(prefix.length)
        val dotPos = remainder.indexOf('.')
        if (dotPos == -1) continue          // malformed; skip
        val idx = remainder.take(dotPos).toIntOrNull() ?: continue
        indices += idx
    }
    return indices.sorted().map { "$prefix$it" }
}



package at.asitplus.gradle.modulator

import org.gradle.api.Project
import org.gradle.api.attributes.Attribute
import java.io.StringReader
import java.io.StringWriter
import java.util.*

internal const val ATTR_PREFIX_MODULATION = "at.asitplus.modulation.v0"
internal const val ATTR_PREFIX_CARRIER = "$ATTR_PREFIX_MODULATION.carrier"
internal val ATTR_CARRIER: Attribute<String> = Attribute.of(ATTR_PREFIX_CARRIER, String::class.java)


internal fun Properties.store(prefix: String? = null): String {
    val prefix = prefix?.let { "$it." } ?: ""
    val os = StringWriter()
    val map = TreeMap<String, Any>()
    forEach { (key, value) -> map["$prefix$key"] = value }
    map.forEach { (key, value) -> os.write("$key=$value\n") }
    return os.toString()
}

internal fun Properties.toAttributesMap(): Map<Attribute<String>, String> =
    map { (key, value) ->
        Attribute.of("$ATTR_PREFIX_MODULATION.$key", String::class.java) to value.toString()
    }.toMap()


data class Modulation(val carriers: List<ModuleDeclaration>, val signal: ModuleDeclaration) :
    Properties() {
    init {
        require(carriers.size <= 2) { "At least two carriers must be declared to modulate a signal. Number of carriers: ${carriers.size}" }

        carriers.forEachIndexed { index, dependencyDeclaration ->
            dependencyDeclaration.forEach { (k, v) ->
                setProperty("carriers.$index.$k", v.toString())
            }
        }
        signal.forEach { (k, v) ->
            setProperty("signal.$k", v.toString())
        }
    }

    companion object {
        fun load(properties: Map<String,String>, prefix: String? = null): Modulation {
            val prefix = prefix?.let { "$it." } ?: ""
            val carriers = properties.keysGroupedByDependencyIndex("${prefix}carriers")
                .map { ModuleDeclaration.load(properties, it) }

            val signalDependency = ModuleDeclaration.load(properties, "${prefix}signal")
            return Modulation(carriers, signalDependency)
        }

        fun load(stringRepresentation: String, prefix: String? = null) = load(Properties().apply {
            load(StringReader(stringRepresentation))
        } as Map<String,String>, prefix)
    }

}

data class ModuleDeclaration(val group: String, val artifact: String, val version: VersionConstraintDeclaration) :
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

    companion object Companion {
        fun load(properties: Map<String, String>, prefix: String? = null): ModuleDeclaration {
            val prefix = prefix?.let { "$it." } ?: ""

            val group = properties["${prefix}group"]
            val artifact = properties["${prefix}artifact"]
            val version = VersionConstraintDeclaration.load(properties, "${prefix}version")
            return ModuleDeclaration(group!!, artifact!!, version)
        }

        fun load(stringRepresentation: String, prefix: String? = null) = load(Properties().apply {
            load(StringReader(stringRepresentation))
        }as Map<String,String>, prefix)
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
        fun load(properties: Map<String,String>, prefix: String? = null): VersionConstraintDeclaration {
            val prefix = prefix?.let { "$it." } ?: ""
            return VersionConstraintDeclaration(
                properties["${prefix}branch"],
                properties["${prefix}requiredVersion"],
                properties["${prefix}preferredVersion"],
                properties["${prefix}strictVersion"],
                properties["${prefix}rejectedVersion"]
            )
        }

        fun load(stringRepresentation: String, prefix: String? = null) = load(Properties().apply {
            load(StringReader(stringRepresentation))
        }as Map<String,String>, prefix)
    }
}

private fun Map<String,String>.keysGroupedByDependencyIndex(prefix: String?): List<String> {
    val prefix = prefix?.let { "$it." } ?: ""
    val indices = mutableSetOf<Int>()

    for (name in keys) {
        if (!name.startsWith(prefix)) continue

        val remainder = name.substring(prefix.length)
        val dotPos = remainder.indexOf('.')
        if (dotPos == -1) continue          // malformed; skip
        val idx = remainder.take(dotPos).toIntOrNull() ?: continue
        indices += idx
    }
    return indices.sorted().map { "$prefix$it" }
}


fun Project.toModuleDeclaration(): ModuleDeclaration {
    val grp = group.toString()
    val moduleName = name
    val ver = version
    return ModuleDeclaration(
        grp.toString(),
        moduleName,
        VersionConstraintDeclaration(
            branch = null,
            rejectedVersion = null,
            preferredVersion = ver.toString(),
            strictVersion = null,
            requiredVersion = null,
        )
    )

}
import at.asitplus.gradle.modulator.DependencyDeclaration
import at.asitplus.gradle.modulator.Modulation
import at.asitplus.gradle.modulator.VersionConstraintDeclaration
import at.asitplus.gradle.modulator.store
import java.io.StringWriter
import java.util.Properties
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ModulationTest {

    @Test
    fun testDatamodel() {
        val ver = VersionConstraintDeclaration(
            branch = "main",
            requiredVersion = null,
            preferredVersion = "0.0.1",
            rejectedVersion = null,
            strictVersion = null
        )
        println(ver.store())
        val verRead = VersionConstraintDeclaration.load(ver.store())
        assertEquals(ver, verRead)

        val dep1 = DependencyDeclaration(group = "at.asitplus.signum", artifact = "indispensable-josef", ver)
        println(dep1.store())

        val dep1Read = DependencyDeclaration.load(dep1.store())
        assertEquals(dep1, dep1Read)

        val dep2 = DependencyDeclaration(
            group = "at.asitplus.signum", artifact = "supreme",
            VersionConstraintDeclaration(null, null, null, null, null)
        )
        println(dep2.store())
        val dep2Read = DependencyDeclaration.load(dep2.store())
        assertEquals(dep2, dep2Read)
        assertNotEquals(dep1, dep2Read)

        val bridge = DependencyDeclaration(group = "at.asitplus.signum", artifact = "josef-supreme", ver)
        println(bridge.store())
        val bridgeRead = DependencyDeclaration.load(bridge.store())
        assertEquals(bridge, bridgeRead)
        assertNotEquals(bridge, dep1Read)
        assertNotEquals(bridge, dep2Read)

        val modulation = Modulation(listOf(dep1, dep2), bridge)
        println(modulation.store())
        val modulationRead = Modulation.load(modulation.store())
        assertEquals(modulation, modulationRead)


    }
}
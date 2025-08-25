import at.asitplus.gradle.modulator.ATTR_PREFIX_MODULATION
import at.asitplus.gradle.modulator.ModuleDeclaration
import at.asitplus.gradle.modulator.Modulation
import at.asitplus.gradle.modulator.VersionConstraintDeclaration
import at.asitplus.gradle.modulator.store
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
        println(ver.store(ATTR_PREFIX_MODULATION))
        val verRead = VersionConstraintDeclaration.load(ver.store(ATTR_PREFIX_MODULATION),ATTR_PREFIX_MODULATION)
        assertEquals(ver, verRead)

        val dep1 = ModuleDeclaration(group = "at.asitplus.signum", artifact = "indispensable-josef", ver)
        println(dep1.store(ATTR_PREFIX_MODULATION))

        val dep1Read = ModuleDeclaration.load(dep1.store(ATTR_PREFIX_MODULATION),ATTR_PREFIX_MODULATION)
        assertEquals(dep1, dep1Read)

        val dep2 = ModuleDeclaration(
            group = "at.asitplus.signum", artifact = "supreme",
            VersionConstraintDeclaration(null, null, null, null, null)
        )
        println(dep2.store(ATTR_PREFIX_MODULATION))
        val dep2Read = ModuleDeclaration.load(dep2.store(ATTR_PREFIX_MODULATION),ATTR_PREFIX_MODULATION)
        assertEquals(dep2, dep2Read)
        assertNotEquals(dep1, dep2Read)

        val bridge = ModuleDeclaration(group = "at.asitplus.signum", artifact = "josef-supreme", ver)
        println(bridge.store(ATTR_PREFIX_MODULATION))
        val bridgeRead = ModuleDeclaration.load(bridge.store(ATTR_PREFIX_MODULATION),ATTR_PREFIX_MODULATION)
        assertEquals(bridge, bridgeRead)
        assertNotEquals(bridge, dep1Read)
        assertNotEquals(bridge, dep2Read)

        val modulation = Modulation(listOf(dep1, dep2), bridge)
        println(modulation.store(ATTR_PREFIX_MODULATION))
        val modulationRead = Modulation.load(modulation.store(ATTR_PREFIX_MODULATION),ATTR_PREFIX_MODULATION)
        assertEquals(modulation, modulationRead)

    }
}
package id.ac.pnj.resortcampus

import id.ac.pnj.resortcampus.data.Badges
import id.ac.pnj.resortcampus.data.PasswordHasher
import id.ac.pnj.resortcampus.data.Points
import id.ac.pnj.resortcampus.data.ai.MockAi
import id.ac.pnj.resortcampus.data.ai.OnDeviceWasteClassifier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CoreLogicTest {
    @Test
    fun passwordHasher_verifiesCorrectAndRejectsWrong() {
        val hash = PasswordHasher.hash("rahasia123")
        assertTrue(PasswordHasher.verify("rahasia123", hash))
        assertFalse(PasswordHasher.verify("salah", hash))
    }

    @Test
    fun pointsRules_matchBrief() {
        assertEquals(15, Points.ruleFor("plastik").points)
        assertEquals(25, Points.ruleFor("elektronik").points)
        assertEquals(5, Points.ruleFor("tidak-dikenal").points)
    }

    @Test
    fun badges_requireAtLeastTenPhotosAndIncreaseProgressively() {
        val all = Badges.ALL
        assertEquals(0, all[0].threshold)
        assertEquals(10, all[1].threshold)
        var prevDelta = all[1].threshold - all[0].threshold
        assertTrue(prevDelta >= 10)
        for (i in 2 until all.size) {
            val delta = all[i].threshold - all[i - 1].threshold
            assertTrue("Badge ${i + 1} delta ($delta) should be > previous ($prevDelta)", delta > prevDelta)
            prevDelta = delta
        }
    }

    @Test
    fun mockAi_isDeterministicForSameHash() {
        val a = MockAi.classify("deadbeef01234567")
        val b = MockAi.classify("deadbeef01234567")
        assertEquals(a, b)
    }

    @Test
    fun onDeviceClassifier_mapsWasteAndNonWasteAccurately() {
        val bottle = OnDeviceWasteClassifier.classifyLabels(listOf("Bottle" to 0.85f, "Drinkware" to 0.72f))
        assertTrue(bottle.isWaste)
        assertEquals("plastik", bottle.category)
        assertEquals("anorganik", bottle.binType)

        val jar = OnDeviceWasteClassifier.classifyLabels(listOf("Jar" to 0.88f, "Bottle" to 0.70f))
        assertTrue(jar.isWaste)
        assertEquals("kaca", jar.category)

        val paper = OnDeviceWasteClassifier.classifyLabels(listOf("Cardboard" to 0.82f, "Paper" to 0.75f))
        assertTrue(paper.isWaste)
        assertEquals("kertas", paper.category)
        assertEquals("kertas", paper.binType)

        val selfie = OnDeviceWasteClassifier.classifyLabels(listOf("Person" to 0.92f, "Face" to 0.89f))
        assertFalse(selfie.isWaste)

        val bin = OnDeviceWasteClassifier.verifyBinLabels(listOf("Waste container" to 0.88f))
        assertTrue(bin.isBin)

        val bucketBin = OnDeviceWasteClassifier.verifyBinLabels(listOf("Bucket" to 0.80f, "Plastic" to 0.72f))
        assertTrue(bucketBin.isBin)

        assertFalse(OnDeviceWasteClassifier.verifyBinLabels(listOf("Bottle" to 0.90f, "Plastic" to 0.85f)).isBin)
        assertFalse(OnDeviceWasteClassifier.verifyBinLabels(listOf("Keyboard" to 0.91f, "Computer" to 0.88f)).isBin)
        assertFalse(OnDeviceWasteClassifier.verifyBinLabels(listOf("Person" to 0.95f, "Face" to 0.92f)).isBin)
        assertFalse(OnDeviceWasteClassifier.verifyBinLabels(emptyList()).isBin)
    }
}

import com.in2000_project.BoatApp.ui.screens.compareTimes
import com.in2000_project.BoatApp.ui.screens.getColor
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class StormvarselKtTest {
    @Test
    fun testCompareTimes() {
        // TODO: Arrange
        val currentInstant = Instant.parse("2023-05-19T12:00:00Z")
        val checkTimeInstant = Instant.parse("2023-05-19T15:00:00Z")

        // TODO: Act
        val result = compareTimes(currentInstant, checkTimeInstant)

        // TODO: Assert
        val expectedDifference = 3 * 60 * 60 // 3 hours in seconds

        assertEquals(expectedDifference.toLong(), result)
    }
    @Test
    fun testGetColor() {
        // TODO: Arrange
        val awarenessLevelGreen = "Low; green"
        val awarenessLevelYellow = "Moderate; yellow"
        val awarenessLevelOrange = "High; orange"
        val awarenessLevelRed = "Extreme; red"
        val awarenessLevelInvalid = "level; unknown"

        // TODO: Act
        val resultGreen = getColor(awarenessLevelGreen)
        val resultYellow = getColor(awarenessLevelYellow)
        val resultOrange = getColor(awarenessLevelOrange)
        val resultRed = getColor(awarenessLevelRed)
        val resultInvalid = getColor(awarenessLevelInvalid)

        // TODO: Assert
        val expectedColorGreen = "#803AF93C"
        val expectedColorYellow = "#80F5D062"
        val expectedColorOrange = "#80F78D02"
        val expectedColorRed = "#80F93C3A"
        val expectedColorInvalid = "#40000000"

        assertEquals(expectedColorGreen, resultGreen)
        assertEquals(expectedColorYellow, resultYellow)
        assertEquals(expectedColorOrange, resultOrange)
        assertEquals(expectedColorRed, resultRed)
        assertEquals(expectedColorInvalid, resultInvalid)
    }
}



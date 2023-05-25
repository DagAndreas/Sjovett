import com.in2000_project.BoatApp.view.screens.compareTimes
import com.in2000_project.BoatApp.view.screens.getColor
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class StormvarselKtTest {
    @Test
    fun testCompareTimes1() {
        // TODO: Arrange
        val currentCalendar = Calendar.getInstance()
        currentCalendar.set(2023, Calendar.MAY, 19, 12, 0, 0)
        val checkTimeCalendar = Calendar.getInstance()
        checkTimeCalendar.set(2023, Calendar.MAY, 19, 15, 0, 0)

        // TODO: Act
        val result = compareTimes(currentCalendar, checkTimeCalendar)

        // TODO: Assert
        val expectedDifference = 3 * 60 * 60 // 3 hours in seconds

        assertEquals(expectedDifference.toLong(), result)
    }
    @Test
    fun testCompareTimes2() {
        // TODO: Arrange
        val currentCalendar = Calendar.getInstance()
        currentCalendar.set(2023, Calendar.MAY, 19, 12, 0, 0)
        val checkTimeCalendar = Calendar.getInstance()
        checkTimeCalendar.set(2023, Calendar.MAY, 18, 12, 0, 0)

        // TODO: Act
        val result = compareTimes(currentCalendar, checkTimeCalendar)

        // TODO: Assert
        val expectedDifference =  24 * 60 * 60 // 24 hours in seconds

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



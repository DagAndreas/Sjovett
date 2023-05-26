
import com.in2000_project.BoatApp.model.oceanforecast.Data
import com.in2000_project.BoatApp.model.oceanforecast.Details
import com.in2000_project.BoatApp.model.oceanforecast.Instant
import com.in2000_project.BoatApp.model.oceanforecast.Timesery
import com.in2000_project.BoatApp.viewmodel.calculateRadius
import com.in2000_project.BoatApp.viewmodel.findClosestDetailsToCurrentTime
import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class MannOverBordKtTest {
    private val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    private val currentTime = Date()

    private val detailsA = Details(
        sea_surface_wave_from_direction = 1.0,
        sea_surface_wave_height = 0.0,
        sea_water_speed = 0.0,
        sea_water_temperature = 0.0,
        sea_water_to_direction = 0.0
    )
    private val detailsB = Details(
        sea_surface_wave_from_direction = 2.0,
        sea_surface_wave_height = 0.0,
        sea_water_speed = 0.0,
        sea_water_temperature = 0.0,
        sea_water_to_direction = 0.0
    )
    private val detailsC = Details(
        sea_surface_wave_from_direction = 3.0,
        sea_surface_wave_height = 0.0,
        sea_water_speed = 0.0,
        sea_water_temperature = 0.0,
        sea_water_to_direction = 0.0
    )
    @Test
    fun testFindClosestDataToTimestamp1() {
        // TODO: Arrange
        val listOfTime = listOf(
            Timesery(
                Data(Instant(detailsA)),
                sdf.format(Date(currentTime.time - 10*60000))
            ),
            Timesery(
                Data(Instant(detailsB)),
                sdf.format(Date(currentTime.time - 5*60000))
            ),
            Timesery(
                Data(Instant(detailsC)),
                sdf.format(Date(currentTime.time + 15*60000))
            )
        )

        // TODO: Act
        val closestData = findClosestDetailsToCurrentTime(listOfTime)

        // TODO: Assert
        val expectedDetails = detailsB // Create the expected Details object

        assertEquals(expectedDetails, closestData)}
    @Test
    fun testFindClosestDataToTimestamp2() {
        // TODO: Arrange
        val listOfTime = listOf(
            Timesery(
                Data(Instant(detailsA)),
                // -6 where 6 is milliseconds. 6*60000 equals 6 minutes
                sdf.format(Date(currentTime.time - 6*60000))
            ),
            Timesery(
                Data(Instant(detailsB)),
                sdf.format(Date(currentTime.time - 5*60000))
            ),
            Timesery(
                Data(Instant(detailsC)),
                sdf.format(Date(currentTime.time - 7*60000))
            )
        )

        // TODO: Act
        val closestData = findClosestDetailsToCurrentTime(listOfTime)

        // TODO: Assert
        val expectedDetails = detailsB // Create the expected Details object

        assertEquals(expectedDetails, closestData)
    }
    @Test
    fun testFindClosestDataToTimestamp3() {
        // TODO: Arrange
        val listOfTime = listOf(
            Timesery(
                Data(Instant(detailsA)),
                // -6 where 6 is milliseconds. 6*60000 equals 6 minutes
                sdf.format(Date(currentTime.time - 10*60000))
            ),
            Timesery(
                Data(Instant(detailsB)),
                sdf.format(Date(currentTime.time + 4*60000))
            ),
            Timesery(
                Data(Instant(detailsC)),
                sdf.format(Date(currentTime.time))
            )
        )

        // TODO: Act
        val closestData = findClosestDetailsToCurrentTime(listOfTime)

        // TODO: Assert
        val expectedDetails = detailsC // Create the expected Details object

        assertEquals(expectedDetails, closestData)
    }
    @Test
    fun testCalculateRadius() {
        // TODO: Arrange
        val testCases = listOf(
            10 to 50.0,   // newRadius = 10 * 5.0 = 50.0
            30 to 150.0,  // newRadius = 30 * 5.0 = 150.0
            5 to 25.0,    // newRadius < 25.0, returns 25.0
            40 to 200.0,  // newRadius > 200.0, returns 200.0
            0 to 25.0     // newRadius < 25.0, returns 25.0
        )

        val delta = 0.0001 // Tolerance for floating-point comparisons

        for ((minutes, expectedRadius) in testCases) {
            // TODO: Act
            val result = calculateRadius(minutes)

            // TODO: Assert
            assertEquals(expectedRadius, result, delta)
        }
    }

}

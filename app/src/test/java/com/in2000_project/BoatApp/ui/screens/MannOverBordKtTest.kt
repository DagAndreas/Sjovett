
import com.in2000_project.BoatApp.model.oceanforecast.Data
import com.in2000_project.BoatApp.model.oceanforecast.Details
import com.in2000_project.BoatApp.model.oceanforecast.Instant
import com.in2000_project.BoatApp.model.oceanforecast.Timesery
import com.in2000_project.BoatApp.viewmodel.findClosestDataToTimestamp
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
        val closestData = findClosestDataToTimestamp(listOfTime)

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
        val closestData = findClosestDataToTimestamp(listOfTime)

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
        val closestData = findClosestDataToTimestamp(listOfTime)

        // TODO: Assert
        val expectedDetails = detailsC // Create the expected Details object

        assertEquals(expectedDetails, closestData)
    }


}

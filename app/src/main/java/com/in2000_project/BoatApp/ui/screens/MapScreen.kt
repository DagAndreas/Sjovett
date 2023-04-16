package com.in2000_project.BoatApp.compose

import android.location.Location
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.in2000_project.BoatApp.viewmodel.MapViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.in2000_project.BoatApp.maps.personHarDriftetTilNesteGrid
import com.in2000_project.BoatApp.model.oceanforecast.Details
import com.in2000_project.BoatApp.model.oceanforecast.Timesery
import com.in2000_project.BoatApp.viewmodel.OceanViewModel
import com.in2000_project.BoatApp.viewmodel.SeaOrLandViewModel
import kotlinx.coroutines.delay
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

const val oceanURL = "https://api.met.no/weatherapi/oceanforecast/2.0/complete" //?lat=60.10&lon=5

@Composable
fun MapScreen(
    viewModel: MapViewModel
) {

    val state by viewModel.state.collectAsState()

    val mapProperties = MapProperties(
        // Only enable if user has accepted location permissions.
        //isMyLocationEnabled = state.lastKnownLocation != null,
        isMyLocationEnabled = true
    )

    Log.d("MapScreen", "$state er staten tidlig")

    var cameraPositionState = rememberCameraPositionState{
        position = CameraPosition.fromLatLngZoom(LatLng(65.0, 11.0), 4f)
    }
    var circleCenter by remember { mutableStateOf(state.circle.coordinates) }
    var circleRadius by remember { mutableStateOf(200.0) }
    var circleVisibility by remember { mutableStateOf(false) }
    var enabled by remember { mutableStateOf(true) }
    var counter by remember { mutableStateOf( 0 ) }

    var mann_er_overbord by remember { mutableStateOf(false)}
    var currentLat: Double
    var currentLong: Double

    if (state.lastKnownLocation != null) {
        currentLat = state.lastKnownLocation!!.latitude
        currentLong = state.lastKnownLocation!!.longitude
    }else{
        Log.i("MapScreen", state.toString())
        currentLat = 59.0646
        currentLong = 10.6778
    }
    val oceanViewModel = OceanViewModel("${oceanURL}?lat=${currentLat}&lon=${currentLong}")

    val apiKeySeaOrLand = "fc0719ee46mshf31ac457f36a8a9p15e288jsn324fc84023ff"
    val latLng = LatLng(58.628244, -9.823267)
    val urlPath = "https://isitwater-com.p.rapidapi.com/?latitude=${latLng.latitude}&longitude=${latLng.longitude}&rapidapi-key=$apiKeySeaOrLand"
    println(urlPath)
    val seaOrLandViewModel = SeaOrLandViewModel(urlPath)

    println("HEI")


    Box(
        /*
        modifier = Modifier
            //.clip(RoundedCornerShape(20.dp))
            .fillMaxSize()
            .padding(bottom = 60.dp /*, start = 20.dp, end = 20.dp, top = 20.dp */)
         */
    ) {
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                //.clip(RoundedCornerShape(20.dp)),
            ,properties = mapProperties,
            cameraPositionState = cameraPositionState
        ) {
            Circle(
                center = circleCenter,
                radius = circleRadius,
                fillColor = Color("#ABF44336".toColorInt()),
                strokeWidth = 2F,
                visible = circleVisibility
            )
        }
    }
    Column() {
        Button(
            onClick = {
                cameraPositionState.position = CameraPosition.fromLatLngZoom(locationToLatLng(state.lastKnownLocation), 13f)

                circleCenter = locationToLatLng(state.lastKnownLocation)
                viewModel.changeCircleCoordinate(locationToLatLng(state.lastKnownLocation)) //unødvendig?
                circleVisibility = true
                enabled = false
                mann_er_overbord = true
            },
            modifier = Modifier
                .wrapContentWidth(CenterHorizontally)
                .padding(start = 160.dp, top = 450.dp)
                .size(90.dp),
            shape = CircleShape,
            colors = ButtonDefaults.outlinedButtonColors(contentColor =  Color.Red),
            border= BorderStroke(1.dp, Color.Red),
            enabled = enabled

        ) {
            Text(
                text = "SOS",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            LaunchedEffect(circleCenter) { //oppdaterer posisjon hvert 3. sek
                while (mann_er_overbord){
                    val time_to_wait_in_minutes: Float = 0.025f //1.0f er 1 minutt. 0.1 = 6sek
                    delay((time_to_wait_in_minutes * 60_000).toLong())
                    //Log.i("MapScreen", "$time_to_wait_in_minutes minutter")
                    counter++
                    circleCenter = calculateNewPosition(circleCenter, oceanViewModel, time_to_wait_in_minutes.toDouble()*2000)
                    circleRadius = calculateRadius(counter)
                }
            }
        }
    }
}
/** når det hentes ny oceanforecdast, så må det sjekkes om det er en null, før den asignes
 * på nytt. */
fun calculateNewPosition(personCoordinate: LatLng, ovm: OceanViewModel, time: Double): LatLng{
    Log.i("MapScreen", "New Pos fra $personCoordinate")
    val dataCoordinate = ovm.oceanForecastResponseObject.geometry.coordinates
    val dataLatLng: LatLng = LatLng(dataCoordinate[1], dataCoordinate[0])

    if (personHarDriftetTilNesteGrid(dataLatLng, personCoordinate)){
        ovm.path = "${oceanURL}?lat=${personCoordinate.latitude}&lon=${personCoordinate.longitude}"
        ovm.getOceanForecastResponse()
    }
    //finner hvilken Timesery (objekt med oceandata) som er nærmeste timestamp
    val forecastDetails = findClosestDataToTimestamp(ovm.oceanForecastResponseObject.properties.timeseries)

    return calculatePosition(listOf(personCoordinate.latitude, personCoordinate.longitude), forecastDetails.sea_surface_wave_from_direction, forecastDetails.sea_water_speed, time)
}
/** henter den listen med bølgedata som er nærmest nåværende klokkeslett */
fun findClosestDataToTimestamp(timeseries: List<Timesery>): Details {

    //TODO: hente riktig dato, finne nærmeste / runde opp til nærmeste tid i listen med timesieries

    //val currentTime = Time.now()
    var closest = timeseries[0]
    //loop through timeseries and find closes time to current timestamp.

    Log.i("MapScreen new details", "${timeseries[0].data.instant.details}")

    //return
    return timeseries[0].data.instant.details

}

/** brukes for å hente posisjonen fra state. default hvis null*/
fun locationToLatLng(loc: Location?): LatLng {
    if (loc != null){ return LatLng(loc.latitude, loc.longitude)}
    return LatLng(59.0, 11.0) //default val i oslofjorden
}

// should find a way to know when it changes grid
// take into account that I assume timeCheckingFor is given in minutes
fun calculatePosition(
        coordinatesStart:List<Double>,
        seaSurfaceWaveToDegrees: Double,
        seaWaterSpeedInMeters: Double,
        timeCheckingFor: Double
    ): LatLng {

    // Convert degrees to radians
    val waveFromInRadians = Math.toRadians(seaSurfaceWaveToDegrees)
    val earthRadiusInKm = 6371
    val startLatInRadians = Math.toRadians(coordinatesStart[0])
    val startLngInRadians = Math.toRadians(coordinatesStart[1])

    // Convert meters per second to kilometers per hour
    val waterSpeedInKmPerHour = seaWaterSpeedInMeters * 3.6

    // Convert the time interval to hours
    val timeIntervalInHours = timeCheckingFor / 60.0

    // Calculate the distance traveled by the object in the given time interval
    val distanceInKm = waterSpeedInKmPerHour * timeIntervalInHours

    // Calculate the new latitude and longitude
    val newLatInRadians = asin(sin(startLatInRadians) * cos(distanceInKm / earthRadiusInKm) + cos(startLatInRadians) * sin(distanceInKm / earthRadiusInKm) * cos(waveFromInRadians))
    val newLngInRadians = startLngInRadians + atan2(sin(waveFromInRadians) * sin(distanceInKm / earthRadiusInKm) * cos(startLatInRadians), cos(distanceInKm / earthRadiusInKm) - sin(startLatInRadians) * sin(newLatInRadians))

    // Convert the new latitude and longitude back to degrees
    val newLat = Math.toDegrees(newLatInRadians)
    val newLng = Math.toDegrees(newLngInRadians)

    return LatLng(newLat, newLng)
}


fun calculateRadius(minutes: Int): Double {
    var newRadius: Double = minutes * 5.0
    return if (newRadius > 200.0) newRadius
    else if (newRadius < 25.0) 25.0
    else newRadius
}







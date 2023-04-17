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
import kotlinx.coroutines.delay
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

const val oceanURL = "https://api.met.no/weatherapi/oceanforecast/2.0/complete" //?lat=60.10&lon=5

@Composable
fun MannOverbord(
    mapViewModel: MapViewModel
) {

    val state by mapViewModel.state.collectAsState()
    val currentLoc = state.lastKnownLocation
    val mapProperties = MapProperties(
        // Only enable if user has accepted location permissions.
        isMyLocationEnabled = state.lastKnownLocation != null
    )

    Log.d("MapScreen", "$state er staten tidlig")

    var cameraZoom: Float = 4f
    val cameraPositionState = rememberCameraPositionState{
        position = CameraPosition.fromLatLngZoom(LatLng(65.0, 11.0), cameraZoom)
    }

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
                center = mapViewModel.circleCenter.value,
                radius = mapViewModel.circleRadius.value,
                fillColor = Color("#ABF44336".toColorInt()),
                strokeWidth = 2F,
                visible = mapViewModel.circleVisibility.value
            )
        }
    }
    Column() {
        Button(
            onClick = {

                //TODO: Bør garantere at vi bruker telefonens nåværende posisjon
                val pos = locationToLatLng(state.lastKnownLocation)
                mapViewModel.oceanViewModel.path = "$oceanURL?lat=${pos.latitude}&lon=${pos.longitude}"
                mapViewModel.oceanViewModel.getOceanForecastResponse()
                Log.i("sender den", "${mapViewModel.oceanViewModel.oceanForecastResponseObject}")

                cameraPositionState.position = CameraPosition.fromLatLngZoom(locationToLatLng(state.lastKnownLocation), 13f)

                mapViewModel.circleCenter.value = locationToLatLng(state.lastKnownLocation)
                //viewModel.changeCircleCoordinate(locationToLatLng(state.lastKnownLocation)) //crasher knappen
                mapViewModel.circleVisibility.value = true
                mapViewModel.enabled.value = false
                mapViewModel.mann_er_overbord.value = true
                mapViewModel.mapUpdateThread.start()

                Log.i("MapScreen button", "Hei fra buttonpress")
            },
            modifier = Modifier
                .wrapContentWidth(CenterHorizontally)
                .padding(start = 160.dp, top = 450.dp)
                .size(90.dp),
            shape = CircleShape,
            colors = ButtonDefaults.outlinedButtonColors(contentColor =  Color.Red),
            border= BorderStroke(1.dp, Color.Red),
            enabled = mapViewModel.enabled.value

        ) {
            Text(
                text = "SOS",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            LaunchedEffect(mapViewModel.circleCenter.value) { //oppdaterer posisjon hvert 3. sek
                Log.i("MapScreen launchedff", "${mapViewModel.mann_er_overbord.value} and in launched effect. Counter is ${mapViewModel.counter.value}")
                if (mapViewModel.followCircle) {
                    cameraPositionState.position =
                        CameraPosition.fromLatLngZoom(mapViewModel.circleCenter.value, 13.0f)
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

    if (timeseries == null){
        Log.d("MapScreen findClosestD", "timeseries listen er null. Fant ikke data, og setter 0 verdier for mann overbord.")
        return (Details(0.0, 0.0, 0.0, 0.0, 0.0))}

    //val currentTime = Time.now()
    var closest = timeseries[0]
    //loop through timeseries and find closes time to current timestamp
    Log.i("MapScreen new details", "${closest.data.instant.details}")
    return timeseries[0].data.instant.details

}

/** brukes for å hente posisjonen fra state. default hvis null*/
fun locationToLatLng(loc: Location?): LatLng {
    if (loc != null){ return LatLng(loc.latitude, loc.longitude)}
    Log.i("locationToLatLng","Fant ingen location. Returnerer default LatLng(59.0, 11.0)")
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







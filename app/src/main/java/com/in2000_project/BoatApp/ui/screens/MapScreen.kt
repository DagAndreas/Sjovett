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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.delay
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MapScreen(
    viewModel: MapViewModel
) {

    val state by viewModel.state.collectAsState()

    val mapProperties = MapProperties(
        // Only enable if user has accepted location permissions.
        isMyLocationEnabled = state.lastKnownLocation != null,
    )

    Log.d("tester", state.lastKnownLocation.toString())

    val cameraPositionState = rememberCameraPositionState{
        //position = CameraPosition.fromLatLngZoom(locationToLatLng(state.lastKnownLocation), 17f)
    }
    var selectedCoordinate by remember { mutableStateOf(state.circle.coordinates) }
    var currentRadius by remember { mutableStateOf(25.0) }
    var circleVisibility by remember { mutableStateOf(false) }
    var enabled by remember { mutableStateOf(true) }
    var counter by remember { mutableStateOf( 0 ) }

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
                center = selectedCoordinate,
                radius = currentRadius,
                fillColor = Color("#ABF44336".toColorInt()),
                strokeWidth = 2F,
                visible = circleVisibility
            )
        }
    }
    Column() {
        Button(
            onClick = {
                selectedCoordinate = locationToLatLng(state.lastKnownLocation)
                viewModel.changeCircleCoordinate(locationToLatLng(state.lastKnownLocation)) //unødvendig?
                circleVisibility = true
                enabled = false
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
            LaunchedEffect(selectedCoordinate) { //oppdaterer posisjon hvert 3. sek
                while(true) {
                    delay(60000)
                    counter++
                    selectedCoordinate = calculateNewPosition(selectedCoordinate)
                    currentRadius = calculateRadius(counter)
                }
            }
        }
    }
}

/**
 * If you want to center on a specific location.
 */

private suspend fun CameraPositionState.centerOnLocation(
    location: Location
) = animate(
    update = CameraUpdateFactory.newLatLngZoom(
        LatLng(location.latitude, location.longitude),
        15f
    ),
)

const val degrees = 180.0
const val seaSpeed = 1.0
const val searchTime = 1.0
fun calculateNewPosition(coordinate: LatLng): LatLng {
    return calculatePosition(listOf(coordinate.latitude, coordinate.longitude), degrees, seaSpeed, searchTime)
}

private fun locationToLatLng(loc: Location?): LatLng {
    return LatLng(loc!!.latitude, loc.longitude)
}

// should find a way to know when it changes grid
// take into account that I assume timeCheckingFor is given in minutes
fun calculatePosition(coordinatesStart:List<Double>,
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

    return if (newRadius > 200.0) 200.0
    else if (newRadius < 25.0) 25.0
    else newRadius

}


private fun test(coordinate: LatLng): LatLng {
    return calculatePosition(listOf(coordinate.latitude, coordinate.longitude), degrees, seaSpeed, searchTime)
}







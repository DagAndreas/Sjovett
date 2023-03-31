package com.codingwithmitch.composegooglemaps.compose

import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.codingwithmitch.composegooglemaps.viewmodel.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlin.math.roundToInt
import kotlin.math.*


@Composable
fun TidsbrukScreen(
    viewModel: MapViewModel
) {

    // Define the state variables
    var knot by remember { mutableStateOf(0f) }
    val state by viewModel.state.collectAsState()
    val mapProperties = MapProperties(
        // Only enable if user has accepted location permissions.
        isMyLocationEnabled = state.lastKnownLocation != null,
    )
    val cameraPositionState = rememberCameraPositionState()
    var myPosition by remember { mutableStateOf(locationToLatLng(state.lastKnownLocation) )}
    var onClickPosition by remember { mutableStateOf(LatLng(0.0, 0.0)) }
    var markerPosition by remember { mutableStateOf<LatLng?>(null) }

    var displayedText by remember {mutableStateOf("Valgt hastighet: ${knot.roundToInt()} knop")}

    // Define the function to update the slider value
    val onKnotChanged: (Float) -> Unit = { value ->
        knot = value.roundToInt().toFloat()
    }

    // Define the function to handle long press on the map
    val onLongPress: (LatLng) -> Unit = { position ->
        onClickPosition = position
        markerPosition = position
        // Do something with the position, e.g. save it to a database
    }

    // Define the UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Velg antall knop:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
        Slider(
            value = knot,
            onValueChange = onKnotChanged,
            valueRange = 0f..50f,
            steps = 50,
            modifier = Modifier
                .fillMaxWidth()
        )
        if (markerPosition==null){
            displayedText = "Valgt hastighet: ${knot.roundToInt()} knop"
        }
        else{
            if(knot==0f){
                displayedText = "Du vil ikke komme fram hvis du kjører 0 knop"
            }
            else{
                displayedText = "Når du kjører fra din posisjon til markøren sin posisjon, i ${knot.toInt()} knop vil du bruke "+ formatTime(calculateTravelTimeInMinutes(myPosition, markerPosition!!, knot))
            }

        }
        Text(
            text = displayedText,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
        Box(
            modifier = Modifier
                //.clip(RoundedCornerShape(20.dp))
                .fillMaxSize()
                .padding(top = 20.dp, bottom = 70.dp, start = 20.dp, end = 20.dp)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                properties = mapProperties,
                cameraPositionState = cameraPositionState,
                onMapLongClick = onLongPress
            ) {
                if (markerPosition != null) {
                    Marker(
                        state = MarkerState(position = markerPosition!!)
                    )
                    Polyline(
                        points = listOf(myPosition, markerPosition!!),
                        color = Color("#ABF44336".toColorInt())
                    )
                }
            }
        }
    }
}



private suspend fun CameraPositionState.centerOnLocation(
    location: Location
) = animate(
    update = CameraUpdateFactory.newLatLngZoom(
        LatLng(location.latitude, location.longitude),
        15f
    ),
)

private fun calculateTravelTimeInMinutes(coordinateA: LatLng, coordinateB: LatLng, knots: Float): Float {
    // Calculate the distance in nautical miles between the two coordinates
    val earthRadiusInNauticalMiles = 3440.06479
    val latDistance = Math.toRadians(coordinateB.latitude - coordinateA.latitude)
    val lngDistance = Math.toRadians(coordinateB.longitude - coordinateA.longitude)
    val a = sin(latDistance / 2) * sin(latDistance / 2) +
            cos(Math.toRadians(coordinateA.latitude)) * cos(Math.toRadians(coordinateB.latitude)) *
            sin(lngDistance / 2) * sin(lngDistance / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    val distanceInNauticalMiles = earthRadiusInNauticalMiles * c

    // Calculate the time in minutes to travel the distance at the given speed
    val speedInNauticalMilesPerMinute = knots / 60f
    val travelTimeInMinutes = distanceInNauticalMiles / speedInNauticalMilesPerMinute
    return travelTimeInMinutes.toFloat()
}
fun formatTime(duration: Float): String {
    return when {
        duration < 60 -> "${duration.roundToInt()} minutter"
        duration < 1440 -> {
            val hours = duration.toInt() / 60
            val minutes = duration.toInt() % 60
            if (minutes == 0) "$hours time(r)" else "$hours time(r) og $minutes minutter"
        }
        else -> {
            val days = duration.toInt() / 1440
            val hours = (duration.toInt() % 1440) / 60
            val minutes = (duration.toInt() % 1440) % 60
            if (hours == 0 && minutes == 0) "$days dag(er)"
            else if (hours == 0) "$days dag(er) og $minutes minutter"
            else if (minutes == 0) "$days dag(er) og $hours minutter"
            else "$days dag(er), $hours time(r) og $minutes minutter"
        }
    }
}

private fun locationToLatLng(loc: Location?): LatLng {
    return LatLng(loc!!.latitude, loc.longitude)
}


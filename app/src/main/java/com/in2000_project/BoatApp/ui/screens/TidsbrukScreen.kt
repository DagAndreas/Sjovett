package com.in2000_project.BoatApp.compose


import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Undo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.compose.*
import com.in2000_project.BoatApp.viewmodel.MapViewModel
import kotlin.math.roundToInt
import kotlin.math.*

@Composable
fun TidsbrukScreen(
    viewModel: MapViewModel = viewModel()
) {
    // Define the state variables
    // start at 15 knots
    var knot by remember { mutableStateOf(15f) }
    val state by viewModel.state.collectAsState()
    val mapProperties = MapProperties(
        // Only enable if user has accepted location permissions.
        isMyLocationEnabled = state.lastKnownLocation != null,
    )

    val cameraPositionState = rememberCameraPositionState()

    // stores position of the user
    var myPosition by remember { mutableStateOf(locationToLatLng(state.lastKnownLocation)!!) }

    // a list containing the markers the user creates
    val markerPositions = remember { mutableStateListOf<LatLng>().apply {add(myPosition)}}

    // a list of lines between the markers
    val polyLines = remember { mutableStateListOf<PolylineOptions>() }

    // text that should be displayed to the user
    var displayedText by remember { mutableStateOf("Du kan legge til en destinasjon ved å holde inne et sted på kartet. ")}
    // distance between all of the markers
    var coordinatesToFindDistanceBetween = remember { mutableStateListOf<LatLng>().apply { add(myPosition) } }
    var distanceInMeters by remember { mutableStateOf(0.0) }
    var lengthInMinutes by remember { mutableStateOf(0.0) }


    // Define the function to update displayed text
    // endre navn på tidsbruk - reiseplanlegger

    fun updateDisplayedText() {

        if (knot == 0f) {
            displayedText = "Du vil ikke komme fram hvis du kjører 0 knop"
        }
        else{
            if(markerPositions.size < 2) {
                displayedText = "Du kan legge til en destinasjon ved å holde inne et sted på kartet. "
            }
            else {
                displayedText = "${formatTime(lengthInMinutes)}"
            }
        }
    }
    // Define the function to update the slider value and displayed text
    val onKnotChanged: (Float) -> Unit = { value ->
        knot = value.roundToInt().toFloat()
        lengthInMinutes = calculateTimeInMinutes(distanceInMeters, knot)
        updateDisplayedText()
    }

    // Define the function to handle long press on the map
    val onLongPress: (LatLng) -> Unit = { position ->
        markerPositions.add(position)
        coordinatesToFindDistanceBetween.add(position)
        val lastPosition = markerPositions[markerPositions.size - 2]
        val options = PolylineOptions()
            .add(lastPosition, position)
            .color(android.graphics.Color.RED)
        polyLines.add(options)
        if (coordinatesToFindDistanceBetween.size > 1) {
            distanceInMeters = calculateDistance(coordinatesToFindDistanceBetween)
            lengthInMinutes = calculateTimeInMinutes(distanceInMeters, knot)
            updateDisplayedText()
        }
    }

    // Define the function to minimize the screen
    fun removeLastMarker() {
        if (markerPositions.size > 1) {
            val removedMarker = markerPositions.removeLast()
            coordinatesToFindDistanceBetween.remove(removedMarker)
            polyLines.removeLast()
            if (coordinatesToFindDistanceBetween.size > 1) {
                distanceInMeters = calculateDistance(coordinatesToFindDistanceBetween)
                lengthInMinutes = calculateTimeInMinutes(distanceInMeters, knot)
            } else {
                distanceInMeters = 0.0
                lengthInMinutes = 0.0
            }
            updateDisplayedText()
        }
    }
// Define the UI
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Antall knop: ${knot.toInt()}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
            IconButton(
                onClick = { removeLastMarker() },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    Icons.Outlined.Undo,
                    contentDescription = "Undo",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Slider(
            value = knot,
            onValueChange = onKnotChanged,
            valueRange = 0f..50f,
            steps = 50,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Text(
            text = displayedText,
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                properties = mapProperties,
                cameraPositionState = cameraPositionState,
                onMapLongClick = onLongPress
            ) {
                markerPositions.forEach { position ->
                    Marker(
                        state = MarkerState(position = position)
                    )
                }
                polyLines.forEach { options ->
                    val points = options.getPoints()
                    Polyline(points)
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

private fun calculateDistance(coordinates: List<LatLng>): Double {
    var distanceInMeters = 0.0
    for (i in 0 until coordinates.size - 1) {
        val lat1 = coordinates[i].latitude
        val lon1 = coordinates[i].longitude
        val lat2 = coordinates[i + 1].latitude
        val lon2 = coordinates[i + 1].longitude
        val dLat = (lat2 - lat1).toRadians()
        val dLon = (lon2 - lon1).toRadians()
        val a = sin(dLat/2) * sin(dLat/2) + cos(lat1.toRadians()) * cos(lat2.toRadians()) * sin(dLon/2) * sin(dLon/2)
        val c = 2 * atan2(sqrt(a), sqrt(1-a))
        distanceInMeters += 6371e3 * c // Earth radius in meters
    }
    return distanceInMeters
}

private fun Double.toRadians(): Double {
    return this * PI / 180
}

fun calculateTimeInMinutes(distanceInMeters: Double, speedInKnots: Float): Double {
    // 1 knot = 0.514444 m/s
    val speedInMetersPerSecond = speedInKnots * 0.514444
    val timeInSeconds = distanceInMeters / speedInMetersPerSecond
    val timeInMinutes = timeInSeconds / 60
    return timeInMinutes
}

fun formatTime(duration: Double): String {
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

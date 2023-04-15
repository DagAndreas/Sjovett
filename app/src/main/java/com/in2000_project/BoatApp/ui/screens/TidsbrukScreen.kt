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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.compose.*
import com.in2000_project.BoatApp.viewmodel.MapViewModel
import kotlin.math.*

@Composable
fun TidsbrukScreen(
    viewModel: MapViewModel = viewModel()
) {
    viewModel.updateLocation()

    // Collect the current state from the view model
    val state by viewModel.state.collectAsState()

// Remember the camera position state so that it persists across recompositions
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(65.0, 14.0), 4f)
    }

// Define the properties of the map
    val mapProperties = MapProperties(
        isMyLocationEnabled = state.lastKnownLocation != null
    )

// Maintain a list of marker positions on the map
    val markerPositions = viewModel.markerPositions.apply {
        if (size == 0) {
            // If the list is empty, add the last known location as the first marker
            add(locationToLatLng(state.lastKnownLocation!!))
        }
    }

// Maintain a list of polylines on the map
    val polyLines = viewModel.polyLines

// Maintain a list of coordinates for calculating the distance between markers
    val coordinatesToFindDistanceBetween = viewModel.coordinatesToFindDistanceBetween.apply {
        if (size == 0) {
            // If the list is empty, add the last known location as the first coordinate
            add(locationToLatLng(state.lastKnownLocation!!))
        }
    }

// Maintain various view model state variables
    val displayedText = viewModel.displayedText
    val distanceInMeters = viewModel.distanceInMeters
    val lengthInMinutes = viewModel.lengthInMinutes
    val speedUnitSelected = viewModel.speedUnitSelected
    val lockMarkers = viewModel.lockMarkers

// Get the position of the last marker if locking is enabled, otherwise it's null
    val lastMarkerPosition = if (lockMarkers.value) markerPositions.last() else null

    // Define a function to update the displayed text based on the current state
    fun updateDisplayedText() {
        if (viewModel.speedNumber.value == 0f) {
            viewModel.displayedText.value = "Du vil ikke komme fram hvis du kjører 0 knop"
        } else {
            if (markerPositions.size < 2) {
                viewModel.displayedText.value = "Du kan legge til en destinasjon ved å holde inne et sted på kartet. "
            } else {
                viewModel.displayedText.value = "Denne ruten vil ta ${formatTime(lengthInMinutes.value)}."
            }
        }
    }

// Define a function to handle changes to the speed slider
    val onSpeedChanged: (Float) -> Unit = { value ->
        viewModel.speedNumber.value = value.roundToInt().toFloat()
        lengthInMinutes.value = calculateTimeInMinutes(distanceInMeters.value, viewModel.speedNumber.value)
        updateDisplayedText()
        viewModel.updateLocation()
    }

// Define a function to handle long presses on the map
    val onLongPress: (LatLng) -> Unit = { position ->
        if (!lockMarkers.value) {
            // Update the current location
            viewModel.updateLocation()

            // Update the first marker position to the current location
            markerPositions[0] = locationToLatLng(state.lastKnownLocation!!)

            // Add the new marker position and coordinate for calculating distance
            markerPositions.add(position)
            coordinatesToFindDistanceBetween.add(position)

            // Add a new polyline between the last two markers
            val lastPosition = markerPositions[markerPositions.size - 2]
            val options = PolylineOptions()
                .add(lastPosition, position)
                .color(android.graphics.Color.RED)

            if(polyLines.size>1){
                val updatedFirstPolyLine = PolylineOptions()
                .add(markerPositions[0], markerPositions[1])
                .color(android.graphics.Color.RED)
                polyLines[0] = updatedFirstPolyLine
            }
            polyLines.add(options)

            if (coordinatesToFindDistanceBetween.size > 1) {
                distanceInMeters.value = calculateDistance(coordinatesToFindDistanceBetween)
                lengthInMinutes.value = calculateTimeInMinutes(distanceInMeters.value, viewModel.speedNumber.value)
            }
            updateDisplayedText()
        }
        //polyLines[0] = locationToLatLng(viewModel.state.value.lastKnownLocation)
    }


    fun removeLastMarker() {
        if (markerPositions.size > 1) {
            val removedMarker = markerPositions.removeLast()
            coordinatesToFindDistanceBetween.remove(removedMarker)
            polyLines.removeLast()
            // update first polyLine
            markerPositions[0] = locationToLatLng(state.lastKnownLocation!!)
            coordinatesToFindDistanceBetween[0] = markerPositions[0]

            if (coordinatesToFindDistanceBetween.size > 1) {
                val updatedPolyLine = PolylineOptions()
                    .add(markerPositions[0], markerPositions[1])
                    .color(android.graphics.Color.RED)
                polyLines[0] = updatedPolyLine
                distanceInMeters.value = calculateDistance(coordinatesToFindDistanceBetween)
                lengthInMinutes.value = calculateTimeInMinutes(distanceInMeters.value, viewModel.speedNumber.value)
            } else {
                distanceInMeters.value = 0.0
                lengthInMinutes.value = 0.0
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
                text = "Antall knop: ${viewModel.speedNumber.value.toInt()}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            IconButton(
                onClick = {
                    if (!viewModel.lockMarkers.value) {
                        removeLastMarker()
                    }
                },
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
            value = viewModel.speedNumber.value,
            onValueChange = onSpeedChanged,
            valueRange = 0f..50f,
            steps = 50,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Text(
            text = viewModel.displayedText.value,
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )
        Button(
            onClick = { viewModel.lockMarkers.value = !viewModel.lockMarkers.value },
            modifier = Modifier.width(150.dp)
        ) {
            if (!viewModel.lockMarkers.value) {
                Text("Lås inn ruten ")
            } else {
                Text("Åpne ruten ")
            }
        }


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
                if (!viewModel.lockMarkers.value) {
                    viewModel.markerPositions.forEach { position ->
                        Marker(
                            state = MarkerState(position = position)
                        )
                    }
                }
                viewModel.polyLines.forEach { options ->
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

// Calculate distance between coordinates
fun calculateDistance(coordinates: List<LatLng>): Double {
    var distance = 0.0
    for (i in 0 until coordinates.lastIndex) {
        val from = coordinates[i]
        val to = coordinates[i + 1]
        val results = FloatArray(1)
        Location.distanceBetween(
            from.latitude,
            from.longitude,
            to.latitude,
            to.longitude,
            results
        )
        distance += results[0]
    }
    return distance
}

// Calculate time in minutes based on distance and speed
fun calculateTimeInMinutes(distanceInMeters: Double, speedInKnots: Float): Double {
    val metersInNauticalMile = 1853
    val minutesInHour = 60
    return (distanceInMeters / (speedInKnots * metersInNauticalMile )) * minutesInHour
}

// Format time in minutes to display as text
fun formatTime(timeInMinutes: Double): String {
    val hours = (timeInMinutes / 60).toInt()
    val minutes = (timeInMinutes % 60).toInt()
    return if (hours == 0) {
        "$minutes minutter"
    } else {
        "$hours timer og $minutes minutter"
    }
}


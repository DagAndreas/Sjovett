package com.codingwithmitch.composegooglemaps.compose

import android.location.Location
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.ViewModel
import com.codingwithmitch.composegooglemaps.MapState
import com.codingwithmitch.composegooglemaps.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

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

    val cameraPositionState = rememberCameraPositionState()
    var selectedCoordinate by remember { mutableStateOf(LatLng(50.0, 50.0)) }
    var circleVisibility by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = mapProperties,
            cameraPositionState = cameraPositionState
        ) {

            Circle(
                center = selectedCoordinate,
                radius = state.circle.radius,
                fillColor = Color("#ABF44336".toColorInt()),
                strokeWidth = 2F,
                visible = circleVisibility
            )

            
        }
    }
    Button(
        onClick = {
            selectedCoordinate = locationToLatLng(state.lastKnownLocation)
            circleVisibility = true
            viewModel.changeCircleCoordinate(selectedCoordinate)
            Log.d("tester2", viewModel.state.toString()) //funker ikke
        },
        modifier = Modifier
            .wrapContentWidth(CenterHorizontally)
            .padding(start = 160.dp, top = 530.dp)
            .size(90.dp),
        shape = CircleShape,
        colors = ButtonDefaults.outlinedButtonColors(contentColor =  Color.Red),
        border= BorderStroke(1.dp, Color.Red)

    ) {
        Text(
            text = "SOS",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
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

private fun locationToLatLng(loc: Location?): LatLng {
    return LatLng(loc!!.latitude, loc!!.longitude)
}


package com.in2000_project.BoatApp.view.screens

import InfoButton
import android.R.attr.*
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import com.in2000_project.BoatApp.R
import com.in2000_project.BoatApp.data.MapState
import com.in2000_project.BoatApp.view.components.InfoPopup
import com.in2000_project.BoatApp.view.components.navigation.MenuButton
import com.in2000_project.BoatApp.viewmodel.MapViewModel
import com.plcoding.bottomnavwithbadges.ui.theme.*
import kotlin.math.*


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TidsbrukScreen(
    viewModel: MapViewModel = viewModel(), openMenu: () -> Unit
) {
    viewModel.updateLocation()

    // Collect the current state from the view model
    val state by viewModel.state.collectAsState()

    // Remember the camera position state so that it persists across recompositions
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(65.0, 14.0), 4f)
    }

    val mapProperties = MapProperties(isMyLocationEnabled = true)


    // The sheet that is on the bottom of the screen
    BottomSheetScaffold(sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        sheetBackgroundColor = White,
        sheetContent = {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .align(CenterHorizontally)
                        .padding(top = 10.dp)
                        .fillMaxWidth(0.2f)
                        .fillMaxHeight(0.01f)
                        .background(
                            color = LightGrey, shape = RoundedCornerShape(5.dp)
                        )
                )
                Text(
                    text = stringResource(R.string.ChooseRoute),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(CenterHorizontally)
                        .padding(top = 5.dp)
                )

                Row(
                    modifier = Modifier.padding(top = 20.dp)
                ) {
                    Text(
                        text = stringResource(R.string.SpeedInKnots) + viewModel.speedNumber.value.toInt(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Row(
                    modifier = Modifier.height(50.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .height(30.dp)
                            .background(
                                color = LightGrey, shape = RoundedCornerShape(40.dp)
                            )
                            .align(Alignment.CenterVertically)

                    ) {
                        // choose knots
                        Slider(
                            value = viewModel.speedNumber.value,
                            onValueChange = viewModel::onSpeedChange,
                            valueRange = 0f..50f,
                            modifier = Modifier
                                .padding(5.dp)
                                .fillMaxWidth(0.5f),
                            colors = SliderDefaults.colors(
                                thumbColor = Grey,
                                activeTickColor = Color.Unspecified,
                                inactiveTickColor = Color.Unspecified,
                                inactiveTrackColor = Color.Unspecified,
                                activeTrackColor = Color.Unspecified,
                                disabledActiveTrackColor = Color.Unspecified
                            )
                        )
                    }

                    viewModel.updateLocation()
                    // Use my location for route or not
                    Checkbox(
                        checked = viewModel.usingMyPositionTidsbruk.value,
                        onCheckedChange = {

                            useOwnLocation(state, viewModel)

                        },
                        modifier = Modifier.align(Alignment.CenterVertically),
                        colors = CheckboxDefaults.colors(
                            uncheckedColor = LightGrey, checkedColor = Green
                        ),
                        enabled = !viewModel.lockMarkers.value
                    )

                    Text(
                        text = stringResource(R.string.StartFromCurrentPosition),
                        fontSize = 13.sp,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                Row(
                    modifier = Modifier.padding(bottom = 20.dp)
                ) {

                    if (!viewModel.lockMarkers.value) {
                        Button(
                            onClick = {
                                viewModel.lockMarkers.value = !viewModel.lockMarkers.value
                            },
                            modifier = Modifier.width(LocalConfiguration.current.screenWidthDp.dp * 0.35f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Green
                            )
                        ) {
                            Text(stringResource(R.string.StartRoute))
                        }
                        // remove marker
                        Button(
                            onClick = {
                                if (!viewModel.lockMarkers.value) {
                                    viewModel.removeLastMarker()
                                }
                            },
                            enabled = !viewModel.markerPositions.isEmpty() && !(viewModel.usingMyPositionTidsbruk.value && viewModel.markerPositions.size == 1),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LightRed, disabledContainerColor = LightGrey
                            ),
                            modifier = Modifier.padding(start = 8.dp)

                        ) {
                            Text(
                                text = stringResource(R.string.RemoveMarker),
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    } else {
                        // lock or unlock route
                        Button(
                            onClick = {
                                viewModel.lockMarkers.value = !viewModel.lockMarkers.value
                            },
                            modifier = Modifier.width(LocalConfiguration.current.screenWidthDp.dp * 0.35f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LightRed
                            )
                        ) {
                            Text(stringResource(R.string.End))
                        }

                        Icon(
                            imageVector = Icons.Outlined.DirectionsBoat,
                            contentDescription = stringResource(R.string.Boat),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(horizontal = 8.dp)
                        )

                        Text(
                            text = viewModel.displayedText.value,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }
        }) {
        Box {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                properties = mapProperties,
                contentPadding = PaddingValues(
                    bottom = LocalConfiguration.current.screenHeightDp.dp * 0.08f,
                    start = 0.dp
                ), //flytter knappene
                cameraPositionState = cameraPositionState,
                onMapLongClick = { latLng -> viewModel.onLongPress(latLng, state) }
            ) {
                val context: ProvidableCompositionLocal<Context> = LocalContext
                if (!viewModel.lockMarkers.value) {
                    viewModel.markerPositions.forEach { position ->
                        Marker(
                            state = MarkerState(position = position)
                        )
                    }
                } else {
                    if (viewModel.markerPositions.size >= 2) {
                        val bitmapStart = BitmapFactory.decodeResource(
                            context.current.resources,
                            R.drawable.start_icon
                        )
                        val bitmapFinish = BitmapFactory.decodeResource(
                            context.current.resources,
                            R.drawable.finish_flag
                        )

                        // Create a scaled bitmap with the desired dimensions
                        val scaledBitmapStart = Bitmap.createScaledBitmap(
                            bitmapStart,
                            64,
                            64,
                            false
                        ) // Change 64 to the desired size of the icon
                        val scaledBitmapFinish = Bitmap.createScaledBitmap(
                            bitmapFinish,
                            64,
                            64,
                            false
                        ) // Change 64 to the desired size of the icon

                        // Create a BitmapDescriptor from the scaled bitmap
                        val iconStartIcon = BitmapDescriptorFactory.fromBitmap(scaledBitmapStart)
                        val iconFinishFlag = BitmapDescriptorFactory.fromBitmap(scaledBitmapFinish)

                        // Use the icon in your Marker
                        Marker(
                            state = MarkerState(position = viewModel.markerPositions.first()),
                            icon = iconStartIcon
                        )

                        Marker(
                            state = MarkerState(position = viewModel.markerPositions.last()),
                            icon = iconFinishFlag
                        )
                    } else {
                        viewModel.displayedText.value = stringResource(R.string.AddMarkers)
                    }

                }

                viewModel.polyLines.forEach { options ->
                    val points = options.points
                    Polyline(points)
                }
            }

            if (viewModel.reiseplanleggerInfoPopup) {
                InfoPopup(
                    mapViewModel = viewModel,
                    screen = stringResource(R.string.ReiseplanleggerScreenName)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.16f)
                    .wrapContentWidth(CenterHorizontally)
                    .padding(top = 10.dp)
            ) {

                MenuButton(
                    buttonIcon = Icons.Filled.Menu,
                    onButtonClicked = { openMenu() },
                    modifier = Modifier
                        .align(CenterHorizontally)
                        .background(
                            color = White, shape = CircleShape
                        )
                        .padding(10.dp)
                        .size(LocalConfiguration.current.screenWidthDp.dp * 0.07f)
                )
                Spacer(Modifier.height(10.dp))

                InfoButton(
                    mapViewModel = viewModel,
                    screen = stringResource(R.string.ReiseplanleggerScreenName)
                )
            }

        }
    }
}

// Calculate distance between coordinates
fun calculateDistance(coordinates: List<LatLng>): Double {
    var distance = 0.0

    // iterates through the list
    for (i in 0 until coordinates.lastIndex) {
        val from = coordinates[i]
        val to = coordinates[i + 1]
        val results = FloatArray(1)
        Location.distanceBetween(
            from.latitude, from.longitude, to.latitude, to.longitude, results
        )
        distance += results[0]
    }
    return distance
}

fun useOwnLocation(state: MapState, viewModel: MapViewModel) {
    viewModel.updateLocation()
    viewModel.updateUseOfCurrentLocation(state)
}


// Calculate time in minutes based on distance and speed
fun calculateTimeInMinutes(distanceInMeters: Double, speedInKnots: Float): Double {
    val metersInNauticalMile = 1853
    val minutesInHour = 60
    return (distanceInMeters / (speedInKnots * metersInNauticalMile)) * minutesInHour
}

// Format time in minutes to display as text
fun formatTime(timeInMinutes: Double): String {
    return if (timeInMinutes < 1) {
        "Under 1 minutt"
    } else {
        val hours = (timeInMinutes / 60).toInt()
        val minutes = (timeInMinutes % 60).toInt()
        if (hours == 0) {
            "$minutes minutter"
        } else {
            "$hours timer og $minutes minutter"
        }
    }
}
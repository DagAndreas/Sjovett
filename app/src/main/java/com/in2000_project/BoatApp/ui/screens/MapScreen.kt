package com.in2000_project.BoatApp.compose

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.core.graphics.toColorInt
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.in2000_project.BoatApp.viewmodel.MapViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.in2000_project.BoatApp.MenuButton
import com.in2000_project.BoatApp.maps.personHarDriftetTilNesteGrid
import com.in2000_project.BoatApp.model.oceanforecast.Details
import com.in2000_project.BoatApp.model.oceanforecast.Timesery
import com.in2000_project.BoatApp.viewmodel.OceanViewModel
import com.in2000_project.BoatApp.viewmodel.SeaOrLandViewModel
import com.in2000_project.BoatApp.viewmodel.locationToLatLng
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

//?lat=60.10&lon=5
const val seaOrLandUrl = "https://isitwater-com.p.rapidapi.com/"

@Composable
fun MannOverbord(
    mapViewModel: MapViewModel,
    openDrawer: () -> Unit
) {
    mapViewModel.updateLocation()

    val state by mapViewModel.state.collectAsState()
    val mapProperties = MapProperties(
        // Only enable if user has accepted location permissions.
        isMyLocationEnabled = true //state.lastKnownLocation != null
    )


    val showDialog = remember { mutableStateOf(false) }

    var cameraZoom: Float = 15f
    val cameraPositionState = rememberCameraPositionState{
    //    position = CameraPosition.fromLatLngZoom(LatLng(65.0, 11.0), cameraZoom)
    }
    val haveZoomedAtStart = remember { mutableStateOf( false )}
    //Log.i("mannoverbord - i ", "${haveZoomedAtStart}")
    Log.i("Circlecenter:", "${mapViewModel.circleCenter.value}")
    if (mapViewModel.oceanViewModel.oceanForecastResponseObject != null) {
        Log.i(
            "MapviewModel data cent:",
            "${mapViewModel.oceanViewModel.oceanForecastResponseObject.geometry.coordinates}"
        )
    }
    Box(

    ) {
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
            ,properties = mapProperties,
            /* contentPadding = PaddingValues(bottom = LocalConfiguration.current.screenHeightDp.dp * 0.75f), //flytter knappene */
            cameraPositionState = cameraPositionState
        ) {
            Circle(
                center = mapViewModel.circleCenter.value,
                radius = mapViewModel.circleRadius.value,
                fillColor = Color("#ABF44336".toColorInt()),
                strokeWidth = 2F,
                visible = mapViewModel.circleVisibility.value
            )
            val polyLinesMapCopy = mapViewModel.polyLinesMap.toList() // Create a copy
            polyLinesMapCopy.forEach { options ->
                val points = options.points
                Polyline(
                    points
                )
            }

        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(start = 10.dp, top = 10.dp)
        ) {
            MenuButton(
                buttonIcon = Icons.Filled.Menu,
                onButtonClicked = { openDrawer() }
            )

            IconButton(
                onClick = { mapViewModel.mannOverBordInfoPopUp = true },
                modifier = Modifier
                    .padding(start = LocalConfiguration.current.screenWidthDp.dp * 0.3f)
            ) {
                Icon(
                    Icons.Outlined.Info,
                    contentDescription = "Info",
                    modifier = Modifier
                        .size(32.dp),
                    tint = Color.White
                )
            }
        }

        if (mapViewModel.mannOverBordInfoPopUp) {
            Popup(
                alignment = Alignment.Center,
                properties = PopupProperties(
                    focusable = true
                )

            ) {
                ElevatedCard(
                    modifier = Modifier
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .width(LocalConfiguration.current.screenWidthDp.dp * 0.6f)
                        .height(LocalConfiguration.current.screenHeightDp.dp * 0.15f)
                        .shadow(
                            elevation = 10.dp,
                            shape = RoundedCornerShape(20.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = { mapViewModel.mannOverBordInfoPopUp = false },
                            modifier = Modifier
                                .align(Alignment.End)
                        ) {
                            Icon(
                                Icons.Outlined.Close,
                                contentDescription = "Close",
                                modifier = Modifier
                                    .size(24.dp),
                                tint = androidx.compose.ui.graphics.Color.Gray
                            )
                        }
                        //Var bare "text før"
                        androidx.compose.material.Text(
                            text = mapViewModel.infoTextMannOverBord,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }

        // Add this state variable
        val showDialog = remember { mutableStateOf(false) }

        Button(
            onClick = {
                mapViewModel.updateLocation()
                val pos = locationToLatLng(state.lastKnownLocation)
                val seaOrLandViewModel = SeaOrLandViewModel("$seaOrLandUrl?latitude=${pos.latitude}&longitude=${pos.longitude}&rapidapi-key=fc0719ee46mshf31ac457f36a8a9p15e288jsn324fc84023ff")

                mapViewModel.viewModelScope.launch {
                    var seaOrLandResponse = seaOrLandViewModel.getSeaOrLandResponse()
                    while (seaOrLandResponse == null) {
                        delay(100)
                        Log.i("MapScreen seaorland", "waiting for seaorlandresponse")
                        seaOrLandResponse = seaOrLandViewModel.getSeaOrLandResponse()
                    }

                    if (seaOrLandResponse?.water == true) {
                        mapViewModel.oceanViewModel.setPath(pos)
                        mapViewModel.oceanViewModel.getOceanForecastResponse()

                        Log.i("sender den", "${mapViewModel.oceanViewModel.oceanForecastResponseObject}")

                        if (!mapViewModel.mapUpdateThread.isRunning) {
                            mapViewModel.startButton(state.lastKnownLocation, pos)
                            mapViewModel.buttonText = "avslutt søk"
                        } else {
                            showDialog.value = true
                        }

                    } else if (seaOrLandResponse?.water == false) {
                        mapViewModel.mannOverBordInfoPopUp = true
                        mapViewModel.infoTextMannOverBord = "Vi kan ikke ta inn bølgedata når du er på land."
                    } else {
                        mapViewModel.mannOverBordInfoPopUp = true
                        mapViewModel.infoTextMannOverBord = "Vi fikk ikke hentet dataene. Prøv igjen!"
                    }
                }
                Log.i("MapScreen button", "Hei fra buttonpress")

            },
            modifier = Modifier
                .wrapContentWidth(CenterHorizontally)
                .padding(
                    top = LocalConfiguration.current.screenHeightDp.dp * 0.73f
                )
                .size(LocalConfiguration.current.screenWidthDp.dp * 0.2f)
                .shadow(
                    elevation = 5.dp,
                    shape = CircleShape
                )
                .align(CenterHorizontally),
            shape = CircleShape,
            colors = ButtonDefaults.outlinedButtonColors(contentColor =  Color.Red),
            enabled = mapViewModel.enabled.value,
        ) {
            Text(
                text = mapViewModel.buttonText,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            LaunchedEffect(haveZoomedAtStart.value) {
                delay(200)
                if (!haveZoomedAtStart.value){
                    haveZoomedAtStart.value = true
                    delay(1000)
                    Log.i("MapScreen", "Zoomer inn på pos")
                    cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(locationToLatLng(state.lastKnownLocation), cameraZoom), 1500)
                }
            }
        }

// Add the AlertDialog
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text("Are you sure?") },
                text = { Text("You are about to restart the search. Do you want to continue?") },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog.value = false
                            mapViewModel.restartButton()
                            mapViewModel.buttonText = "start søk"
                        }
                    ) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDialog.value = false }
                    ) {
                        Text("No")

                    }
                }
            )
        }
    }
}
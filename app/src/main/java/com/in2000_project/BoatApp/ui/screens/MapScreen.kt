package com.in2000_project.BoatApp.compose

import AvsluttSok
import AvsluttSokPopup
import InfoButton
import NavigationMenuButton
import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Info
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
import com.in2000_project.BoatApp.maps.personHarDriftetTilNesteGrid
import com.in2000_project.BoatApp.model.oceanforecast.Details
import com.in2000_project.BoatApp.model.oceanforecast.Timesery
import com.in2000_project.BoatApp.ui.components.InfoPopup
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

    val locationObtained = remember { mutableStateOf(false) }
    mapViewModel.updateLocation()
    locationObtained.value = true


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

            NavigationMenuButton(
                buttonIcon = Icons.Filled.Menu,
                onButtonClicked = { openDrawer() }
            )

            InfoButton(
                mapViewModel = mapViewModel,
                screen = "Mann-over-bord"
            )
        }

        if (mapViewModel.mannOverBordInfoPopUp) {
            InfoPopup(
                mapViewModel = mapViewModel,
                screen = "Mann-over-bord"
            )
        }


        AvsluttSok(
            mapViewModel = mapViewModel,
            state = state,
            locationObtained = locationObtained,
            modifier = Modifier
                .wrapContentWidth(Alignment.CenterHorizontally)
                .padding(
                    top = LocalConfiguration.current.screenHeightDp.dp * 0.73f
                )
                .size(LocalConfiguration.current.screenWidthDp.dp * 0.2f)
                .shadow(
                    elevation = 5.dp,
                    shape = CircleShape
                )
                .align(Alignment.CenterHorizontally),
            cameraPositionState = cameraPositionState,
            cameraZoom = cameraZoom
        )

// Add the AlertDialog
        if (mapViewModel.showDialog) {
            AvsluttSokPopup(
                mapViewModel = mapViewModel
            )
        }
    }
}
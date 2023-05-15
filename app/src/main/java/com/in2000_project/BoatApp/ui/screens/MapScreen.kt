package com.in2000_project.BoatApp.compose

import AvsluttSok
import AvsluttSokPopup
import InfoButton
import NavigationMenuButton
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.*
import com.in2000_project.BoatApp.R
import com.in2000_project.BoatApp.ui.components.InfoPopup
import com.in2000_project.BoatApp.viewmodel.MapViewModel
import com.plcoding.bottomnavwithbadges.ui.theme.OpacityRed
import com.plcoding.bottomnavwithbadges.ui.theme.White

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
                fillColor = OpacityRed,
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
        /*
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
                screen = stringResource(R.string.MannOverBordScreenName)
            )
        }

         */

        Column(
            modifier = Modifier
                .fillMaxWidth(0.16f)
                .wrapContentWidth(CenterHorizontally)
                .padding(top = 10.dp)
        ) {

            NavigationMenuButton(
                buttonIcon = Icons.Filled.Menu,
                onButtonClicked = { openDrawer() },
                modifier = Modifier
                    .align(CenterHorizontally)
                    .background(
                        color = White,
                        shape = CircleShape
                    )
                    .padding(10.dp)
                    .size(LocalConfiguration.current.screenWidthDp.dp * 0.07f)
            )

            InfoButton(
                mapViewModel = mapViewModel,
                screen = stringResource(R.string.MannOverBordScreenName)
            )
        }


        if (mapViewModel.mannOverBordInfoPopUp) {
            InfoPopup(
                mapViewModel = mapViewModel,
                screen = stringResource(R.string.MannOverBordScreenName)
            )
        }


        AvsluttSok(
            mapViewModel = mapViewModel,
            state = state,
            locationObtained = locationObtained,
            modifier = Modifier
                .wrapContentWidth(CenterHorizontally)
                .padding(
                    top = LocalConfiguration.current.screenHeightDp.dp * 0.66f
                )
                .size(LocalConfiguration.current.screenWidthDp.dp * 0.2f)
                .shadow(
                    elevation = 5.dp,
                    shape = CircleShape
                )
                .align(CenterHorizontally),
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
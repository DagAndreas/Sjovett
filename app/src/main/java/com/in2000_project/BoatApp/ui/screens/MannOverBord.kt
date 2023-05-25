package com.in2000_project.BoatApp.ui.screens

import EndSearch
import InfoButton
import com.in2000_project.BoatApp.ui.components.navigation.NavigationMenuButton
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.maps.android.compose.*
import com.in2000_project.BoatApp.R
import com.in2000_project.BoatApp.launch.CheckInternet
import com.in2000_project.BoatApp.launch.InternetPopupState
import com.in2000_project.BoatApp.ui.components.InfoPopup
import com.in2000_project.BoatApp.ui.components.info.NoInternetPopup
import com.in2000_project.BoatApp.ui.components.manoverboard.EndSearchPopup
import com.in2000_project.BoatApp.viewmodel.MapViewModel
import com.plcoding.bottomnavwithbadges.ui.theme.OpacityRed
import com.plcoding.bottomnavwithbadges.ui.theme.White
import java.util.*

const val seaOrLandUrl = "https://isitwater-com.p.rapidapi.com/"

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun MannOverbord(
    mapViewModel: MapViewModel,
    openDrawer: () -> Unit,
    connection: CheckInternet,
    internetPopupState: InternetPopupState
) {

    mapViewModel.updateLocation()

    val state by mapViewModel.state.collectAsState()
    /*
    isMyLocationEnabled is set to always true in this version of the code,
    this is due to some of our emulators inconsistency to remember that
    "allow use of location" was enabled

    This is the line that would be in the finished product:
    isMyLocationEnabled = mapState.lastKnownLocation != null
     */
    val mapProperties = MapProperties(isMyLocationEnabled = true)

    val locationObtained = remember { mutableStateOf(false) }
    mapViewModel.updateLocation()
    if(state.lastKnownLocation!=null && !locationObtained.value){
        locationObtained.value = true
        Log.i("galksjd", "headsd")
    }



    val cameraZoom = 15f
    val cameraPositionState = rememberCameraPositionState{}
    Log.i("Circlecenter", "${mapViewModel.circleCenter.value}")

    Box {
        GoogleMap(
            modifier = Modifier
                .fillMaxSize(),
            properties = mapProperties,
            cameraPositionState = cameraPositionState
        ) {
            Circle(
                center = mapViewModel.circleCenter.value,
                radius = mapViewModel.circleRadius.value,
                fillColor = OpacityRed,
                strokeWidth = 2F,
                visible = mapViewModel.circleVisibility.value
            )
            val polyLinesMapCopy = mapViewModel.polyLinesMap.toList()
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
        Column(
            modifier = Modifier
                .fillMaxWidth(1.0f)
                .wrapContentWidth(Alignment.Start)
                .padding(top = 10.dp, start = 10.dp)
        ) {
            Box(modifier = Modifier
                .fillMaxWidth(1.0f)
                .fillMaxHeight(0.09f)
            ) {

                NavigationMenuButton(
                    buttonIcon = Icons.Filled.Menu,
                    onButtonClicked = { openDrawer() },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .background(
                            color = White,
                            shape = CircleShape
                        )
                        .padding(10.dp)
                        .size(LocalConfiguration.current.screenWidthDp.dp * 0.07f)
                )
                val timePassedInSeconds = mapViewModel.timePassedInSeconds.value
                val minutes = timePassedInSeconds / 60
                val seconds = timePassedInSeconds % 60
                val timePassedFormatted =
                    String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

                Text(
                    text = "${stringResource(R.string.TimeSearchedMessage)} $timePassedFormatted",
                    Modifier
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                        .padding(8.dp)
                        .align(Alignment.TopCenter),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            InfoButton(
                mapViewModel = mapViewModel,
                screen = stringResource(R.string.ManOverboardScreenName)
            )
        }

        if (mapViewModel.manIsOverboardInfoPopup) {
            InfoPopup(
                mapViewModel = mapViewModel,
                screen = stringResource(R.string.ManOverboardScreenName)
            )
        }

        EndSearch(
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
            cameraZoom = cameraZoom,
            connection = connection,
            internetPopupState = internetPopupState
        )

        // Add the AlertDialog
        if (mapViewModel.showDialog) {
            EndSearchPopup(
                mapViewModel = mapViewModel
            )
        }

        if (internetPopupState.checkInternetPopup.value) {
            NoInternetPopup(
                internetPopupState = internetPopupState
            )
        }
    }
}
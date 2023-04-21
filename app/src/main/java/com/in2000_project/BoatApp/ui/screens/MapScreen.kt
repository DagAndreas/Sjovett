package com.in2000_project.BoatApp.compose

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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.in2000_project.BoatApp.viewmodel.MapViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.in2000_project.BoatApp.MenuButton
import com.in2000_project.BoatApp.maps.personHarDriftetTilNesteGrid
import com.in2000_project.BoatApp.model.oceanforecast.Details
import com.in2000_project.BoatApp.model.oceanforecast.Timesery
import com.in2000_project.BoatApp.viewmodel.OceanViewModel
import kotlinx.coroutines.delay
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

const val oceanURL = "https://api.met.no/weatherapi/oceanforecast/2.0/complete" //?lat=60.10&lon=5


@Composable
fun MannOverbord(
    mapViewModel: MapViewModel,
    openDrawer: () -> Unit
) {
    Log.i("mannoverbord - i ", "${mapViewModel.i++}")
    mapViewModel.updateLocation()

    val state by mapViewModel.state.collectAsState()
    val mapProperties = MapProperties(
        // Only enable if user has accepted location permissions.
        isMyLocationEnabled = true //state.lastKnownLocation != null
    )

    Log.d("MapScreen", "$state er staten tidlig")

    var cameraZoom: Float = 15f
    val cameraPositionState = rememberCameraPositionState{
    //    position = CameraPosition.fromLatLngZoom(LatLng(65.0, 11.0), cameraZoom)
    }
    var haveZoomedAtStart = false
    //Log.i("mannoverbord - i ", "${haveZoomedAtStart}")

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
            mapViewModel.polyLinesMap.forEach { options ->
                val points = options.getPoints()
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
                            text = "test",
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }

        Button(
            onClick = {

                //TODO: Bør garantere at vi bruker telefonens nåværende posisjon
                val pos = locationToLatLng(state.lastKnownLocation)
                mapViewModel.oceanViewModel.path = "$oceanURL?lat=${pos.latitude}&lon=${pos.longitude}"
                mapViewModel.oceanViewModel.getOceanForecastResponse()
                Log.i("sender den", "${mapViewModel.oceanViewModel.oceanForecastResponseObject}")

                mapViewModel.circleCenter.value = locationToLatLng(state.lastKnownLocation)
                //viewModel.changeCircleCoordinate(locationToLatLng(state.lastKnownLocation)) //crasher knappen
                mapViewModel.circleVisibility.value = true
                mapViewModel.enabled.value = false
                mapViewModel.mann_er_overbord.value = true
                mapViewModel.markersMapScreen.add(pos)
                mapViewModel.mapUpdateThread.start()

                Log.i("MapScreen button", "Hei fra buttonpress")
            },
            modifier = Modifier
                .wrapContentWidth(CenterHorizontally)
                .padding(
                    /*start = LocalConfiguration.current.screenWidthDp.dp * 0.4f,*/
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
                text = "Start søk",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            LaunchedEffect(haveZoomedAtStart) { //oppdaterer posisjon hvert 3. sek
                delay(200)
                if (!haveZoomedAtStart){
                    haveZoomedAtStart = true
                    delay(1000)
                    Log.i("MapScreen", "Zoomer inn på pos")
                    cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(locationToLatLng(state.lastKnownLocation), cameraZoom), 1500)
                }


                Log.i("MapScreen launchedff", "${mapViewModel.mann_er_overbord.value} and in launched effect. Counter is ${mapViewModel.timePassedInSeconds.value}")
            }
        }
    }
}
/** når det hentes ny oceanforecdast, så må det sjekkes om det er en null, før den asignes
 * på nytt. */
fun calculateNewPosition(personCoordinate: LatLng, ovm: OceanViewModel, time: Double): LatLng{
    Log.i("MapScreen", "New Pos fra $personCoordinate")
    val dataCoordinate = ovm.oceanForecastResponseObject.geometry.coordinates
    val dataLatLng: LatLng = LatLng(dataCoordinate[1], dataCoordinate[0])

    if (personHarDriftetTilNesteGrid(dataLatLng, personCoordinate)){
        ovm.path = "${oceanURL}?lat=${personCoordinate.latitude}&lon=${personCoordinate.longitude}"
        ovm.getOceanForecastResponse()
    }
    //finner hvilken Timesery (objekt med oceandata) som er nærmeste timestamp
    val forecastDetails = findClosestDataToTimestamp(ovm.oceanForecastResponseObject.properties.timeseries)

    return calculatePosition(listOf(personCoordinate.latitude, personCoordinate.longitude), forecastDetails.sea_surface_wave_from_direction, forecastDetails.sea_water_speed, time)
}
/** henter den listen med bølgedata som er nærmest nåværende klokkeslett */
fun findClosestDataToTimestamp(timeseries: List<Timesery>): Details {

    //TODO: hente riktig dato, finne nærmeste / runde opp til nærmeste tid i listen med timesieries

    if (timeseries == null){
        Log.d("MapScreen findClosestD", "timeseries listen er null. Fant ikke data, og setter 0 verdier for mann overbord.")
        return (Details(0.0, 0.0, 0.0, 0.0, 0.0))}

    //val currentTime = Time.now()
    var closest = timeseries[0]
    //loop through timeseries and find closes time to current timestamp
    Log.i("MapScreen new details", "${closest.data.instant.details}")
    return timeseries[0].data.instant.details

}

/** brukes for å hente posisjonen fra state. default hvis null*/
fun locationToLatLng(loc: Location?): LatLng {
    if (loc != null){ return LatLng(loc.latitude, loc.longitude)}
    Log.i("locationToLatLng","Fant ingen location. Returnerer default LatLng(59.0, 11.0)")
    return LatLng(59.0, 11.0) //default val i oslofjorden
}

// should find a way to know when it changes grid
// take into account that I assume timeCheckingFor is given in minutes
fun calculatePosition(
        coordinatesStart:List<Double>,
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
    val newRadius: Double = minutes * 5.0
    return if (newRadius > 200.0) 200.0
    else if (newRadius < 25.0) 25.0
    else newRadius
}







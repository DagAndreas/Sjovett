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

    //Log.d("MapScreen", "$state er staten tidlig")

    var cameraZoom: Float = 15f
    val cameraPositionState = rememberCameraPositionState{
    //    position = CameraPosition.fromLatLngZoom(LatLng(65.0, 11.0), cameraZoom)
    }
    var haveZoomedAtStart = false
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

        Button(
            onClick = {

                //TODO: Bør garantere at vi bruker telefonens nåværende posisjon
                mapViewModel.updateLocation()
                val pos = locationToLatLng(state.lastKnownLocation)
                val seaOrLandViewModel = SeaOrLandViewModel("$seaOrLandUrl?latitude=${pos.latitude}&longitude=${pos.longitude}&rapidapi-key=fc0719ee46mshf31ac457f36a8a9p15e288jsn324fc84023ff")

                // launch a coroutine to get the response from the API
                mapViewModel.viewModelScope.launch {
                    var seaOrLandResponse = seaOrLandViewModel.getSeaOrLandResponse()
                    // wait for the response to be returned
                    while (seaOrLandResponse == null) {
                        delay(100) // wait for 100 milliseconds before checking again
                        seaOrLandResponse = seaOrLandViewModel.getSeaOrLandResponse()
                    }

                    // process the response
                    if (seaOrLandResponse?.water == true) {
                        // the coordinate is on water
                        mapViewModel.oceanViewModel.setPath(pos)
                        mapViewModel.oceanViewModel.getOceanForecastResponse()

                        Log.i("sender den", "${mapViewModel.oceanViewModel.oceanForecastResponseObject}")

                        if (!mapViewModel.mapUpdateThread.isRunning) {
                            mapViewModel.startButton(state.lastKnownLocation, pos)
                        } else (
                                mapViewModel.restartButton()
                        )

                    } else if (seaOrLandResponse?.water == false) {
                        // the coordinate is on land
                        mapViewModel.mannOverBordInfoPopUp = true
                        mapViewModel.infoTextMannOverBord = "Vi kan ikke ta inn bølgedata når du er på land."
                    } else {
                        // there was an error getting the response
                        mapViewModel.mannOverBordInfoPopUp = true
                        mapViewModel.infoTextMannOverBord = "Vi fikk ikke hentet dataene. Prøv igjen!"
                    }
                }
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
            }
        }
    }
}
/** Når det hentes ny oceanforecdast, så må det sjekkes om det er en null, før den asignes
 * på nytt. */
fun calculateNewPosition(personCoordinate: LatLng, ovm: OceanViewModel, time: Double): LatLng{
    Log.i("MapScreen", "New Pos fra $personCoordinate")
    val dataCoordinate = ovm.oceanForecastResponseObject.geometry.coordinates
    val dataLatLng: LatLng = LatLng(dataCoordinate[1], dataCoordinate[0])

    if (personHarDriftetTilNesteGrid(dataLatLng, personCoordinate)){
        ovm.setPath(personCoordinate)
        ovm.getOceanForecastResponse()
    }
    //finner hvilken Timesery (objekt med oceandata) som er nærmeste timestamp
    val forecastDetails = findClosestDataToTimestamp(ovm.oceanForecastResponseObject.properties.timeseries)

    return calculatePosition(listOf(personCoordinate.latitude, personCoordinate.longitude), forecastDetails.sea_surface_wave_from_direction, forecastDetails.sea_water_speed, time)
}

/** henter den listen med bølgedata som er nærmest nåværende klokkeslett */
@SuppressLint("SimpleDateFormat")
fun findClosestDataToTimestamp(listOfTime: List<Timesery>): Details {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    val currentTime = Date()
    Log.i("Current time", "$currentTime")
    var i = 0

    for (item in listOfTime) {
        val checkTime: Date
        try {
            checkTime = sdf.parse(item.time) as Date
        } catch (e: ParseException) {
            e.printStackTrace()
            continue
        }

        val secondsBetween = getSecondsBetween(currentTime, checkTime)
        if (secondsBetween >= 0) {
            Log.i("found closest time:", "${listOfTime[i].time}")
            return listOfTime[i].data.instant.details
        }
        i++
    }
    return listOfTime[0].data.instant.details
}
fun getSecondsBetween(date1: Date, date2: Date): Long {
    val diffInMilliseconds = date1.time - date2.time
    return TimeUnit.MILLISECONDS.toSeconds(diffInMilliseconds)
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







package com.in2000_project.BoatApp.ui.screens

//package com.example.StormWarning

import android.content.Context
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.in2000_project.BoatApp.viewmodel.MetAlertsViewModel
import com.in2000_project.BoatApp.R
import com.in2000_project.BoatApp.viewmodel.LocationForecastViewModel
import com.example.gruppe_16.model.locationforecast.Timesery
import com.example.gruppe_16.model.metalerts.Geometry
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.ktx.model.polygonOptions
import com.in2000_project.BoatApp.ZoneClusterManager
import com.in2000_project.BoatApp.viewmodel.AlertsMapViewModel
import kotlinx.coroutines.launch

import java.util.*


// latitude = north-south
var userLat = 57.3 // disse skal endres til brukerens faktiske lokasjon
// longitude = east-west
var userLng = 7.0


@Composable
fun StormWarning(
    viewModelAlerts: MetAlertsViewModel,
    viewModelForecast: LocationForecastViewModel,
    viewModelMap: AlertsMapViewModel,
    setupClusterManager: (Context, GoogleMap) -> ZoneClusterManager,
    calculateZoneViewCenter: () -> LatLngBounds,
    modifier: Modifier
){
    // hentet fra MapScreen:
    val mapState by viewModelMap.state.collectAsState()
    val mapProperties = MapProperties(
        // Only enable if user has accepted location permissions.
        isMyLocationEnabled = mapState.lastKnownLocation != null,
    )
    val cameraPositionState = rememberCameraPositionState{
        //position = CameraPosition.fromLatLngZoom(locationToLatLng(state.lastKnownLocation), 17f)
    }
    //slutt på hentet fra MapScreen

    var placeInput by remember{ mutableStateOf("") }
    val stormWarningUiState = viewModelAlerts.stormWarningUiState.collectAsState()
    val temperatureUiState = viewModelForecast.temperatureUiState.collectAsState()
    val warnings = stormWarningUiState.value.warningList
    val temperatureData = temperatureUiState.value.timeList
    val configuration = LocalConfiguration.current

    Log.d("LISTEN", temperatureData.toString())

    // Therese start

    //val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    //val currentTime = sdf.format(Calendar.getInstance())

    //Log.d("lov", currentTime.toString())

    // val chosenTime = chooseTime(times)
    var temp = 0.0
    var windSpeed = 0.0
    var windDirection = 0.0
    if (temperatureData != emptyList<Timesery>()) {
        temp = temperatureData[0].data.instant.details.air_temperature
        windSpeed = temperatureData[0].data.instant.details.wind_speed
        windDirection = 90.0 + temperatureData[0].data.instant.details.wind_from_direction
        Log.d("WindDir", "${windDirection-90}")
        Log.d("truls", temperatureData[0].data.instant.details.air_temperature.toString())
    }
    // Therese slutt
    Column(modifier = modifier,
        //verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        for(warning in warnings){
            if(warning.properties.geographicDomain == "marine" && checkIfCloseToWarning(warning.geometry)) {
                //StormTextCard(warning.properties.area)
                Log.d("Lokasjon", warning.properties.area)
                viewModelMap.addCluster( // her må det endres litt
                    id = warning.properties.area,
                    title = warning.properties.description,
                    description = warning.properties.consequences,
                    polygonOptions = polygonOptions {
                        for (item in warning.geometry.coordinates) {
                            for (coordinate in item) {
                                add(LatLng(coordinate[1], coordinate[0]))
                            }
                        }
                        fillColor(0xFFFFFFF)
                    }
                )
                Log.d("Koordinater", warning.geometry.toString())
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Column(modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ForecastTextField( //her burde det være en form for dropdown-meny, slik at hvis man begynner å skrive så bør det dukke opp forslag
                value = placeInput,
                onValueChange = {placeInput = it}
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = placeInput,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier
                    .border(
                        BorderStroke(2.dp, Color.Black),
                        shape = RoundedCornerShape(15.dp)
                    )
                    .padding(16.dp)


            ){
                Image(
                    painter = painterResource(id = R.drawable.img_cloud_sun),
                    contentDescription = "Picture of cloud and sun",
                    modifier = Modifier
                        .wrapContentSize()
                        .size(75.dp)
                )
                Spacer(modifier = Modifier.width(20.dp))

                Text(
                    "$temp C°",
                    fontSize = 50.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    "$windSpeed m/s",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Light
                )
                Image(
                    painter = painterResource(id = R.drawable.baseline_arrow_right_alt_24),
                    contentDescription = "Wind arrow",
                    modifier = Modifier
                        .wrapContentSize()
                        .size(100.dp)
                        .graphicsLayer(
                            rotationZ = windDirection.toFloat()
                        )

                )
            }
            Spacer(modifier = Modifier.height(30.dp))

            GoogleMap(
                modifier = Modifier
                    .height(configuration.screenWidthDp.dp)
                //.clip(RoundedCornerShape(20.dp)),
                ,properties = mapProperties,
                cameraPositionState = cameraPositionState
            ) {
                val context = LocalContext.current
                val scope = rememberCoroutineScope()
                MapEffect(mapState.clusterItems) { map ->
                    if (mapState.clusterItems.isNotEmpty()) {
                        val clusterManager = setupClusterManager(context, map)
                        map.setOnCameraIdleListener(clusterManager)
                        map.setOnMarkerClickListener(clusterManager)
                        mapState.clusterItems.forEach { clusterItem ->
                            map.addPolygon(clusterItem.polygonOptions)
                        }
                        map.setOnMapLoadedCallback {
                            if (mapState.clusterItems.isNotEmpty()) {
                                scope.launch {
                                    cameraPositionState.animate(
                                        update = CameraUpdateFactory.newLatLngBounds(
                                            calculateZoneViewCenter(),
                                            0
                                        ),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

    }// Lazy
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForecastTextField(
    value: String,
    onValueChange: (String) -> Unit
){
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = {Text(text = "Skriv inn sted")}, //endre
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
    )
}

@Composable
fun StormTextCard(area: String) {
    Text(area)
}


fun checkIfCloseToWarning(geometry: Geometry): Boolean {
    // NB! Her kan vi lage en test
    /*
    Denne brukes til å sjekke om brukeren er i nærheten av utkanten til en storm

    BUG: Dersom brukeren er midt i en stor storm
     */
    val acceptableOffset = 2 // Tall for å si om noe skal informeres spesielt om til brukeren
    val borders = findBorders(geometry, acceptableOffset)
    val northernBorder = borders[0]
    val southernBorder = borders[1]
    val easternBorder = borders[2]
    val westernBorder = borders[3]
    Log.d("StromWarningBorders", "  $southernBorder < $userLat < $northernBorder")
    Log.d("StromWarningBorders", "  $westernBorder < $userLng < $easternBorder")
    if (
        (userLat < northernBorder && southernBorder < userLat)
        && (userLng < easternBorder && westernBorder < userLng)
    ) {
        Log.d("StromWarningBorders", "FOUND!")
        return true
    }
/*
    Kommentert ut: id1
 */
    return false
}

fun findBorders(
    listOfCoordinates: Geometry,
    acceptableOffset: Int
): List<Double>{
    var northernBorder = -100.0 // Nordpolen = 90
    var southernBorder = 100.0 // Sydpolen = -90
    var easternBorder = -200.0 // Østligste punkt = 180
    var westernBorder = 200.0 // Vestligste punkt = -180

    var northernPoint = Array<Double>(2){0.0}
    var southernPoint = Array<Double>(2){0.0}
    var easternPoint = Array<Double>(2){0.0}
    var westernPoint = Array<Double>(2){0.0}

    for (item in listOfCoordinates.coordinates) {
        for (coordinates in item) {
            val itemLng = coordinates[0]
            val itemLat = coordinates[1]
            if (itemLat > northernBorder) {
                northernPoint[0] = itemLng
                northernPoint[1] = itemLat
                northernBorder = itemLat
            }
            if (itemLat < southernBorder) {
                southernPoint[0] = itemLng
                southernPoint[1] = itemLat
                southernBorder = itemLat
            }
            if (itemLng > easternBorder) {
                easternPoint[0] = itemLng
                easternPoint[1] = itemLat
                easternBorder = itemLng
            }
            if (itemLng < westernBorder) {
                westernPoint[0] = itemLng
                westernPoint[1] = itemLat
                westernBorder = itemLng
            }
        }
    }
    northernBorder += acceptableOffset
    southernBorder -= acceptableOffset
    easternBorder += acceptableOffset
    westernBorder -= acceptableOffset
    Log.d("StromWarningBorders", "N: ${northernPoint[0]}:${northernPoint[1]}, S: ${southernPoint[0]}:${southernPoint[1]}, E: ${easternPoint[0]}:${easternPoint[1]}, W: ${westernPoint[0]}:${westernPoint[1]}")
    return listOf(northernBorder, southernBorder, easternBorder, westernBorder)
}

/*
fun chooseTime(timeList: List<Timesery>): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")

    val currentTime = sdf.format(Calendar.getInstance())
    Log.d("klokk", currentTime.toString())
    var closestDate: String = ""
    var closestDiff: Double? = null

    for(item in timeList){
        val date = item.time
        val diff = abs(date.toDouble() - currentTime.toDouble())
        if(closestDiff == null || diff < closestDiff){
            closestDate = item.toString()
            closestDiff = diff
        }
    }

    return closestDate
}
*/


/* id1
    for (item in listOfCoordinates) {
        Log.d("listOfCoordinates", "Her")
        for (coordinates in item) {
            Log.d("item", "Der")
            val itemLng = coordinates[0]
            val itemLat = coordinates[1]

            if (
                (((itemLat-userLat) <= acceptableOffset && (userLat-itemLat) <= acceptableOffset))
                && ((itemLng-userLng) <= acceptableOffset) && (userLng-itemLng) <= acceptableOffset) {

                Log.d("Square", "Lat: $itemLat ($userLat), Lng: $itemLng ($userLng)")
                return true
            }


        }
    }
 */
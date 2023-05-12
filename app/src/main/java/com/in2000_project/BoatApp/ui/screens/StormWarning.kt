package com.in2000_project.BoatApp.ui.screens

import InfoButtonStorm
import NavigationMenuButton
import WeatherCard
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.KeyEvent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.Top
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.gruppe_16.model.locationforecast.Timesery
import com.example.gruppe_16.model.metalerts.Feature
//import com.example.gruppe_16.model.metalerts.Geometry
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import com.google.maps.android.ktx.model.polygonOptions

import com.in2000_project.BoatApp.ZoneClusterManager
import com.in2000_project.BoatApp.model.geoCode.City
import com.in2000_project.BoatApp.ui.components.InfoPopup
import com.in2000_project.BoatApp.ui.components.InfoPopupStorm
import com.in2000_project.BoatApp.viewmodel.AlertsMapViewModel
import com.in2000_project.BoatApp.viewmodel.LocationForecastViewModel
import com.in2000_project.BoatApp.viewmodel.MetAlertsViewModel
import com.in2000_project.BoatApp.viewmodel.SearchViewModel
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.*


@SuppressLint("PotentialBehaviorOverride")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, MapsComposeExperimentalApi::class)
@Composable
fun StormWarning(
    viewModelAlerts: MetAlertsViewModel,
    viewModelForecast: LocationForecastViewModel,
    viewModelMap: AlertsMapViewModel,
    viewModelSearch: SearchViewModel,
    setupClusterManager: (Context, GoogleMap) -> ZoneClusterManager,
    calculateZoneViewCenter: () -> LatLngBounds,
    modifier: Modifier,
    openDrawer: () -> Unit
){
    val mapProperties = MapProperties(isMyLocationEnabled = true/*mapState.lastKnownLocation != null,*/)
    val mapState by viewModelMap.state.collectAsState()
    if (mapState.lastKnownLocation != null) {
        viewModelMap.updateUserLocation(mapState.lastKnownLocation!!.latitude, mapState.lastKnownLocation!!.longitude)
    }
    val cameraPositionState = rememberCameraPositionState{
        position = CameraPosition.fromLatLngZoom(LatLng(65.0, 14.0), 4f)
    }
    val myPosition = viewModelMap.alertsMapUiState.collectAsState()
    var userLat by remember{ mutableStateOf(myPosition.value.latitude) }
    var userLng by remember{ mutableStateOf(myPosition.value.longitude) }
    val stormWarningUiState = viewModelAlerts.stormWarningUiState.collectAsState()
    val temperatureUiState = viewModelForecast.temperatureUiState.collectAsState()
    val geoCodeUiState = viewModelSearch.geoCodeUiState.collectAsState()
    val locationSearch = viewModelSearch.locationSearch.collectAsState()
    val cities = viewModelSearch.cities.collectAsState()
    val searchInProgress = viewModelSearch.searchInProgress.collectAsState().value
    val warnings = stormWarningUiState.value.warningList
    val temperatureData = temperatureUiState.value.timeList
    var cityData: List<City>
    val configuration = LocalConfiguration.current
    var location by remember { mutableStateOf("Oslo") }
    var openSearch by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    viewModelForecast.updateUserCoord(userLat, userLng)
    addStormClusters(viewModelMap = viewModelMap, warnings = warnings)


    Column(modifier = modifier,
        horizontalAlignment = CenterHorizontally
    ){
        Row(
            modifier = Modifier
                .padding(start = 10.dp, top = 10.dp)
                .align(Alignment.Start)
        ) {
            NavigationMenuButton(
                buttonIcon = Icons.Filled.Menu,
                onButtonClicked = { openDrawer() }
            )

            InfoButtonStorm(
                alertsMapViewModel = viewModelMap
            )
        }

        if (viewModelMap.stormvarselInfoPopUp) {
            InfoPopupStorm(
                alertsMapViewModel = viewModelMap
            )
        }

        Column(
            modifier = modifier.fillMaxSize()
            ,
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = CenterHorizontally
        ){
            TextField(
                value = locationSearch.value,
                onValueChange = { newSearchText ->
                    viewModelSearch.onSearchChange(newSearchText)
                    openSearch = true
                },
                placeholder = { Text(text = "Søk på sted") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                singleLine = true,
                modifier = Modifier.onKeyEvent{
                    if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER){
                        focusManager.clearFocus()
                    }
                    true
                },
            )
            if (searchInProgress) {
                    Box {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                } else {
                    if (openSearch) {
                        LazyColumn(
                            modifier = Modifier
                                .align(CenterHorizontally)
                                .fillMaxWidth()
                                .background(
                                    color = androidx.compose.ui.graphics.Color(0xFFF2F2F2),
                                    shape = RoundedCornerShape(20.dp)
                                )
                        ) {
                            items(cities.value) { CityName ->
                                Text(
                                    text = "${CityName.name}, ${CityName.country}",
                                    modifier = Modifier
                                        .padding(vertical = 16.dp)
                                        .fillMaxWidth()
                                        .clickable {

                                            openSearch = false
                                            focusManager.clearFocus()

                                            location = CityName.name
                                            cityData = emptyList()
                                            Log.d("temper", location)

                                            CoroutineScope(Dispatchers.IO).launch {
                                                viewModelSearch.fetchCityData(CityName.name)
                                                while (geoCodeUiState.value.cityList.isEmpty()) {
                                                    delay(100) // Wait for 100 milliseconds before checking again
                                                }
                                                cityData = geoCodeUiState.value.cityList
                                                if (cityData.isNotEmpty()) {
                                                    userLat = cityData[0].latitude
                                                    userLng = cityData[0].longitude
                                                    viewModelForecast.updateUserCoord(
                                                        userLat,
                                                        userLng
                                                    )


                                                }
                                                viewModelSearch.resetCityData()
                                            }
                                        }
                                )
                                Divider(color = Black, thickness = 0.9.dp)
                            }
                        }

                    }
                }

            Column(
                horizontalAlignment = CenterHorizontally
            ){
                Spacer(modifier = Modifier.height(0.025 * configuration.screenHeightDp.dp))
                Text(
                    text = "Været de neste 24 timene",
                    fontWeight = FontWeight.Bold,
                    fontSize = 0.05 * configuration.screenWidthDp.sp
                )
                Spacer(modifier = Modifier.height(0.025 * configuration.screenHeightDp.dp))
                Box(
                    modifier = Modifier.height(0.3 * configuration.screenHeightDp.dp)
                ){
                    LazyRow(
                        Modifier.fillMaxHeight()
                    ){
                        val timeMap = indexClosestTime(temperatureData)
                        for (key in timeMap.keys) {
                            item {
                                WeatherCard(
                                    time = "${timeMap[key]}",
                                    temperature = temperatureData[key].data.instant.details.air_temperature,
                                    windSpeed = temperatureData[key].data.instant.details.wind_speed,
                                    windDirection = temperatureData[key].data.instant.details.wind_from_direction,
                                    gustSpeed = temperatureData[key].data.instant.details.wind_speed_of_gust,
                                    rainAmount = temperatureData[key].data.next_1_hours.details.precipitation_amount,
                                    weatherIcon = temperatureData[key].data.next_1_hours.summary.symbol_code
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
                Box(
                    modifier = Modifier.height(0.5 * configuration.screenHeightDp.dp)
                ){
                    if (warnings.isNotEmpty()){
                        GoogleMap(
                            modifier = Modifier
                                .height(configuration.screenWidthDp.dp),
                            properties = mapProperties,
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
                }
            }
        }
    }
}


/*
@SuppressLint("ResourceType")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun chooseTimeOfDay(viewModelSearch: SearchViewModel): String { //Fra Therese oblig 1
    val a = stringArrayResource(id = R.array.timeOfDayArray)
    val options = listOf(a[0], a[1], a[2])
    var expanded by remember { mutableStateOf(false) }
    var valgtOption by remember { mutableStateOf(options[0]) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        Modifier.fillMaxWidth()

    ) {
        TextField(
            readOnly = true,
            value = valgtOption,
            onValueChange = {},
            label = { Text("Label") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        valgtOption = selectionOption
                        expanded = false
                        viewModelSearch.resetCityData()
                    },
                )
            }
        }
    }

    return valgtOption
}*/

@RequiresApi(Build.VERSION_CODES.O)
fun createInstant(date: String): Instant? {
    val currentTimeData = date.removeSuffix("Z").split("T")
    val currentYear = currentTimeData[0].split("-")[0].toInt()
    val currentMonth = currentTimeData[0].split("-")[1].toInt()
    val currentDay = currentTimeData[0].split("-")[2].toInt()
    val currentHour = currentTimeData[1].split(":")[0].toInt()
    val currentMinute = currentTimeData[1].split(":")[1].toInt()
    val currentSecond = currentTimeData[1].split(":")[2].toInt()
    return Instant.ofEpochSecond(0)
        .atZone(ZoneOffset.UTC)
        .withYear(currentYear)
        .withMonth(currentMonth)
        .withDayOfMonth(currentDay)
        .withHour(currentHour)
        .withMinute(currentMinute)
        .withSecond(currentSecond)
        .toInstant()
}

@SuppressLint("SimpleDateFormat")
@RequiresApi(Build.VERSION_CODES.O)
fun indexClosestTime(listOfTime: List<Timesery>): MutableMap<Int, Instant> {
    val returnMap = mutableMapOf<Int, Instant>() // Will contain index for time now, every three hours up to 24 hours
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    val time = sdf.format(Date())
    val currentTime = createInstant(time)!!
    var wantedBetween = 0
    var found = 0
    for ((i, item) in listOfTime.withIndex()) {
        val checkTime = createInstant(item.time)!!
        val secondsBetween = compareTimes(currentTime, checkTime)
        if(secondsBetween >= wantedBetween) {
            found++
            if(wantedBetween == 0) {
                wantedBetween = 60*60*3 // 60 seconds * 60 to get 1 hour * 3 to get 3 hours
            } else {
                wantedBetween += 60*60*3 // 3 hours in the future
            }
            returnMap[i] = checkTime
            if (found >= 8){ // 8 will give us close to 24 hour coverage
                return returnMap
            }
        }
    }
    return returnMap
}

@RequiresApi(Build.VERSION_CODES.O)
fun compareTimes(currentInstant: Instant, checkTimeInstant: Instant): Long {
// find the difference in seconds
    return ChronoUnit.SECONDS.between(currentInstant, checkTimeInstant)
}


fun getColor(awarenessLevel: String): String {
    val color = awarenessLevel.split("; ")[1]
    Log.d("Color", color)

    return when (color) {
        "green" -> "#803AF93C"
        "yellow" -> "#80F5D062"
        "orange" -> "#80F78D02"
        "red" -> "#80F93C3A"
        else -> "#40000000"
    }
}

fun addStormClusters(
    viewModelMap: AlertsMapViewModel,
    warnings: List<Feature>
) {
    viewModelMap.resetCluster()
    for(warning in warnings){
        Log.i("Warning at location", "${warning.properties.area} - ${warning.properties.geographicDomain}")
        if(warning.properties.geographicDomain == "marine" || warning.properties.geographicDomain == "land" /*&& checkIfCloseToWarning(warning.geometry)*/){ // checkIfCloseToWarning viser kun de i nærhetenn av brukeren
            viewModelMap.addCluster(
                id = warning.properties.area,
                title = warning.properties.area,
                description = warning.properties.instruction,
                polygonOptions = polygonOptions {
                    for (item in warning.geometry.coordinates) {
                        for (coordinate in item) {
                            add(LatLng(coordinate[1], coordinate[0]))
                        }
                    }
                    fillColor(Color.parseColor((getColor(warning.properties.awareness_level))))
                    strokeWidth(0.5f)
                }
            )
        }
    }
}

//TODO: kan bruke denne for å sleppe minimum sdk 26.
/*
fun createCalendar(date: String): Calendar? {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    val parsedDate = sdf.parse(date) ?: return null
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    calendar.time = parsedDate
    return calendar
}

@SuppressLint("SimpleDateFormat")
fun indexClosestTime(listOfTime: List<Timesery>): MutableMap<Int, Calendar> {
    val returnMap = mutableMapOf<Int, Calendar>()
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    val time = sdf.format(Date())
    val currentTime = createCalendar(time)!!
    var wantedBetween = 0L
    var found = 0
    var i = 0
    for (item in listOfTime) {
        val checkTime = createCalendar(item.time)!!
        val secondsBetween = compareTimes(currentTime, checkTime)
        if (secondsBetween >= wantedBetween) {
            found++
            if (wantedBetween == 0L) {
                wantedBetween = 60 * 60 * 3
            } else {
                wantedBetween += 60 * 60 * 3
            }
            returnMap[i] = checkTime
            if (found >= 8) {
                return returnMap
            }
        }
        i++
    }
    return returnMap
}

fun compareTimes(currentCalendar: Calendar, checkTimeCalendar: Calendar): Long {
    val diffMillis = checkTimeCalendar.timeInMillis - currentCalendar.timeInMillis
    return diffMillis / 1000
}*/

/*
fun findBorders(
    listOfCoordinates: Geometry,
    acceptableOffset: Int
): List<Double>{
    var northernBorder = -100.0 // Nordpolen = 90
    var southernBorder = 100.0 // Sydpolen = -90
    var easternBorder = -200.0 // Østligste punkt = 180
    var westernBorder = 200.0 // Vestligste punkt = -180

    val northernPoint = Array(2){0.0}
    val southernPoint = Array(2){0.0}
    val easternPoint = Array(2){0.0}
    val westernPoint = Array(2){0.0}

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
 */

/*
//todo: unused function
fun checkIfCloseToWarning(
    geometry: Geometry,
    userLat: Double,
    userLng: Double
): Boolean {
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
*/
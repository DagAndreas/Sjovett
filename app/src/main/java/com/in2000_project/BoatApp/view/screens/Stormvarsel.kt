package com.in2000_project.BoatApp.view.screens

import InfoButtonStorm
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.KeyEvent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.example.gruppe_16.model.locationforecast.Timesery
import com.example.gruppe_16.model.metalerts.Feature
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.maps.android.ktx.model.polygonOptions
import com.in2000_project.BoatApp.R
import com.in2000_project.BoatApp.launch.CheckInternet
import com.in2000_project.BoatApp.launch.InternetPopupState
import com.in2000_project.BoatApp.maps.ZoneClusterManager
import com.in2000_project.BoatApp.model.geoCode.City
import com.in2000_project.BoatApp.view.components.InfoPopupStorm
import com.in2000_project.BoatApp.view.components.info.NoInternetPopup
import com.in2000_project.BoatApp.view.components.navigation.MenuButton
import com.in2000_project.BoatApp.view.components.stormvarsel.WeatherCard
import com.in2000_project.BoatApp.viewmodel.*
import com.plcoding.bottomnavwithbadges.ui.theme.LightGrey
import com.plcoding.bottomnavwithbadges.ui.theme.White
import io.ktor.util.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

/** Represents the function that shows weather- and storm-reports */
@SuppressLint("PotentialBehaviorOverride")
@OptIn(ExperimentalMaterial3Api::class, MapsComposeExperimentalApi::class)
@Composable
fun Stormvarsel(
    viewModelAlerts: MetAlertsViewModel,
    viewModelForecast: LocationForecastViewModel,
    viewModelMap: AlertsMapViewModel,
    viewModelSearch: SearchViewModel,
    setupClusterManager: (Context, GoogleMap) -> ZoneClusterManager,
    modifier: Modifier,
    openMenu: () -> Unit,
    connection: CheckInternet,
    internetPopupState: InternetPopupState
) {
    viewModelMap.updateLocation()
    /**
    isMyLocationEnabled is set to always true in this version of the code,
    this is due to some of our emulators inconsistency to remember that
    "allow use of location" was enabled

    This is the line that would be in the finished product:
    isMyLocationEnabled = mapState.lastKnownLocation != null
     */
    val mapProperties = MapProperties(isMyLocationEnabled = true)
    val mapState by viewModelMap.state.collectAsState()
    var userLat = mapState.lastKnownLocation?.latitude ?: 59.9139
    var userLng = mapState.lastKnownLocation?.longitude ?: 10.7522
    val stormvarselUiState = viewModelAlerts.stormvarselUiState.collectAsState()
    val temperatureUiState = viewModelForecast.temperatureUiState.collectAsState()
    val geoCodeUiState = viewModelSearch.geoCodeUiState.collectAsState()
    val locationSearch = viewModelSearch.locationSearch.collectAsState()
    val cities = viewModelSearch.cities.collectAsState()
    val searchInProgress = viewModelSearch.searchInProgress.collectAsState().value
    val warnings = stormvarselUiState.value.warningList
    val temperatureData = temperatureUiState.value.timeList
    var cityData: List<City>
    val configuration = LocalConfiguration.current
    var location by remember { mutableStateOf("Oslo") }
    var openSearch by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    var firstSearch by remember { mutableStateOf(true) }
    if (firstSearch) {
        viewModelForecast.updateWeatherDataBasedOnCoordinate(
            userLat,
            userLng,
            connection,
            internetPopupState
        )
        firstSearch = false
        addStormClusters(viewModelMap = viewModelMap, warnings = warnings)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(userLat, userLng), 7f)
    }

    Column(
        modifier = modifier, horizontalAlignment = CenterHorizontally
    ) {
        if (viewModelMap.stormvarselInfoPopUp) {
            InfoPopupStorm(
                alertsMapViewModel = viewModelMap
            )
        }

        if (internetPopupState.checkInternetPopup.value) {
            NoInternetPopup(
                internetPopupState = internetPopupState
            )
        }

        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = CenterHorizontally
        ) {

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(start = 8.dp)
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

                    InfoButtonStorm(
                        alertsMapViewModel = viewModelMap
                    )
                }

                TextField(
                    value = locationSearch.value,
                    onValueChange = { newSearchText ->
                        viewModelSearch.onSearchChange(newSearchText)
                        openSearch = true
                    },
                    placeholder = { Text(text = stringResource(R.string.Search)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    singleLine = true,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 10.dp)
                        .onKeyEvent {
                            if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                                focusManager.clearFocus()
                            }
                            true
                        },
                )
            }

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
                                color = LightGrey, shape = RoundedCornerShape(20.dp)
                            )
                    ) {


                        items(cities.value) { CityName ->
                            Text(text = "${CityName.name}, ${CityName.country}",
                                modifier = Modifier
                                    .padding(vertical = 16.dp)
                                    .fillMaxWidth()
                                    .clickable {

                                        openSearch = false
                                        focusManager.clearFocus()

                                        location = CityName.name
                                        cityData = emptyList()

                                        CoroutineScope(Dispatchers.IO).launch {
                                            viewModelSearch.fetchCityData(
                                                CityName.name, connection, internetPopupState
                                            )
                                            while (geoCodeUiState.value.cityList.isEmpty()) {
                                                delay(100) // Wait for 100 milliseconds before checking again
                                            }
                                            cityData = geoCodeUiState.value.cityList
                                            if (cityData.isNotEmpty()) {
                                                userLat = cityData[0].latitude
                                                userLng = cityData[0].longitude
                                                viewModelForecast.updateWeatherDataBasedOnCoordinate(
                                                    userLat, userLng, connection, internetPopupState
                                                )
                                            }
                                            viewModelSearch.resetCityData()
                                        }
                                    })
                            Divider(color = Black, thickness = 0.9.dp)
                        }
                    }

                }
            }

            Column(
                horizontalAlignment = CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(0.025 * configuration.screenHeightDp.dp))
                Text(
                    text = stringResource(R.string.WeatherNext24H),
                    fontWeight = FontWeight.Bold,
                    fontSize = 0.05 * configuration.screenWidthDp.sp
                )
                Spacer(modifier = Modifier.height(0.025 * configuration.screenHeightDp.dp))
                Box(
                    modifier = Modifier.height(0.3 * configuration.screenHeightDp.dp)
                ) {
                    LazyRow(
                        Modifier.fillMaxHeight()
                    ) {
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
                ) {

                    GoogleMap(
                        modifier = Modifier.height(configuration.screenWidthDp.dp),
                        properties = mapProperties,
                        cameraPositionState = cameraPositionState
                    ) {
                        if (warnings.isNotEmpty()) {
                            val context = LocalContext.current
                            MapEffect(mapState.clusterItems) { map ->
                                if (mapState.clusterItems.isNotEmpty()) {
                                    val clusterManager = setupClusterManager(context, map)
                                    map.setOnCameraIdleListener(clusterManager)
                                    map.setOnMarkerClickListener(clusterManager)
                                    mapState.clusterItems.forEach { clusterItem ->
                                        map.addPolygon(clusterItem.polygonOptions)
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

/** Creates a Calendar-object based on the date-string */
fun createCalendar(date: String): Calendar? {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    val parsedDate = sdf.parse(date) ?: return null
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    calendar.time = parsedDate
    return calendar
}

/** Finds the data from the Timesery that is closest to "now" */
@SuppressLint("SimpleDateFormat")
fun indexClosestTime(listOfTime: List<Timesery>): MutableMap<Int, Date> {
    val returnMap = mutableMapOf<Int, Date>()
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.GERMANY)
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    val time = sdf.format(Date())
    val currentTime = createCalendar(time)!!
    var wantedBetween = 0
    var found = 0

    for ((i, item) in listOfTime.withIndex()) {
        val checkTime = createCalendar(item.time)!!
        val secondsBetween = compareTimes(currentTime, checkTime)
        if (secondsBetween >= wantedBetween) {
            found++
            wantedBetween += 60 * 3 // 60 seconds * 60 to get 1 hour * 3 to get 3 hours
            returnMap[i] = checkTime.time
            if (found >= 8) {
                return returnMap
            }
        }
    }
    return returnMap
}

/** Compares two Calendars and returns the difference between them in seconds */
fun compareTimes(currentCalendar: Calendar, checkTimeCalendar: Calendar): Long {
    val diffMillis = checkTimeCalendar.timeInMillis - currentCalendar.timeInMillis
    return diffMillis / (1000 * 60)
}

/** Formats the awarnessLevel into the right color-code */
fun getColor(awarenessLevel: String): String {
    return when (awarenessLevel.split("; ")[1]) {
        "green" -> "#803AF93C"
        "yellow" -> "#80F5D062"
        "orange" -> "#80F78D02"
        "red" -> "#80F93C3A"
        else -> "#40000000"
    }
}

/** Adds stormClusters to the viewModelMap at the right coordinates */
fun addStormClusters(
    viewModelMap: AlertsMapViewModel, warnings: List<Feature>
) {
    viewModelMap.resetCluster()
    for (warning in warnings) {
        Log.i(
            "Warning at location",
            "${warning.properties.area} - ${warning.properties.geographicDomain}"
        )
        if (warning.properties.geographicDomain == "marine") {
            viewModelMap.addCluster(id = warning.properties.area,
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
                })
        }
    }
}
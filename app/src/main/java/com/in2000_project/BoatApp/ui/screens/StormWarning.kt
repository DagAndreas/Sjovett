package com.in2000_project.BoatApp.ui.screens

//package com.example.StormWarning

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
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
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.provider.SyncStateContract.Helpers.update
import android.view.KeyEvent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
//import androidx.compose.foundation.layout.RowScopeInstance.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.Top
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.google.android.gms.maps.model.CameraPosition
import com.in2000_project.BoatApp.viewmodel.SearchViewModel
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import com.in2000_project.BoatApp.MenuButton
import com.in2000_project.BoatApp.model.geoCode.City
import java.util.*


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
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
    // hentet fra MapScreen:
    val mapState by viewModelMap.state.collectAsState()
    val mapProperties = MapProperties(
        // Only enable if user has accepted location permissions.
        isMyLocationEnabled = true//mapState.lastKnownLocation != null, // denne er null - finner brukerens posisjon dersom den settes til true
    )

    if (mapState.lastKnownLocation != null) {
        // bruke viewModel.updateLocation()???
        viewModelMap.updateUserLocation(mapState.lastKnownLocation!!.latitude, mapState.lastKnownLocation!!.longitude)
    }


    Log.d("tester", mapState.lastKnownLocation.toString())

    val cameraPositionState = rememberCameraPositionState{
        position = CameraPosition.fromLatLngZoom(LatLng(65.0, 14.0), 4f)
    }
    //slutt på hentet fra MapScreen
    // nullpointerException
    // var myPosition by remember { mutableStateOf(locationToLatLng(mapState.lastKnownLocation)!!) }

    var myPosition = viewModelMap.alertsMapUiState.collectAsState()
    Log.d("myPosition", "${myPosition.value.latitude},${myPosition.value.longitude}")

    var userLat by remember{ mutableStateOf(myPosition.value.latitude) }
    var userLng by remember{ mutableStateOf(myPosition.value.longitude) }
    viewModelForecast.updateUserCoord(userLat, userLng)

    val stormWarningUiState = viewModelAlerts.stormWarningUiState.collectAsState()
    val temperatureUiState = viewModelForecast.temperatureUiState.collectAsState()
    val geoCodeUiState = viewModelSearch.geoCodeUiState.collectAsState()
    var locationSearch = viewModelSearch.locationSearch.collectAsState()
    val cities = viewModelSearch.cities.collectAsState()
    val searchInProgress = viewModelSearch.searchInProgress.collectAsState().value
    val warnings = stormWarningUiState.value.warningList
    val temperatureData = temperatureUiState.value.timeList
    var temperatureCoord = temperatureUiState.value.coords
    var cityData = geoCodeUiState.value.cityList
    val configuration = LocalConfiguration.current
    var location by remember { mutableStateOf("Oslo") }

    var openSearch by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    //var timeOfDay by remember{ mutableStateOf("Nå")}
    //var timeOfDay = "Nå"

    Column(modifier = modifier,
        //verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        viewModelMap.resetCluster()
        for(warning in warnings){
            Log.d("Lokasjon Warning", "${warning.properties.area} - ${warning.properties.geographicDomain}")
            if(warning.properties.geographicDomain == "marine" || warning.properties.geographicDomain == "land" /*&& checkIfCloseToWarning(warning.geometry)*/){ // checkIfCloseToWarning viser kun de i nærhetenn av brukeren
                //StormTextCard(warning.properties.area)
                Log.d("Lokasjon", warning.properties.area)
                Log.d("Lokasjon", warning.toString())
                viewModelMap.addCluster( // her må det endres litt
                    id = warning.properties.area,
                    title = warning.properties.area?: "Unknown",
                    description = warning.properties.instruction?: "Unknown",
                    polygonOptions = polygonOptions {
                        for (item in warning.geometry.coordinates) {
                            for (coordinate in item) {
                                add(LatLng(coordinate[1], coordinate[0]))
                            }
                        }
                        fillColor(Color.parseColor((getColor(warning.properties.awareness_level)))) //endrer farge/density
                        //fillColor(Color.parseColor("#40F93C3A")) //endrer farge/density
                        strokeWidth(0.5f) //endrer bredde på kant
                    }
                )
                Log.d("Koordinater", warning.geometry.toString() )
            }
        }

        Row(
            modifier = Modifier
                .padding(start = 10.dp, top = 10.dp)
                .align(Alignment.Start)
        ) {
            MenuButton(
                buttonIcon = Icons.Filled.Menu,
                onButtonClicked = { openDrawer() }
            )

            IconButton(
                onClick = { viewModelMap.stormvarselInfoPopUp = true },
                modifier = Modifier
                    .padding(start = LocalConfiguration.current.screenWidthDp.dp * 0.3f)
            ) {
                Icon(
                    Icons.Outlined.Info,
                    contentDescription = "Info",
                    modifier = Modifier
                        .size(32.dp),
                    tint = androidx.compose.ui.graphics.Color.Black
                )
            }
        }

        if (viewModelMap.stormvarselInfoPopUp) {
            Popup(
                alignment = Alignment.Center,
                properties = PopupProperties(
                    focusable = true
                )

            ) {
                ElevatedCard(
                    modifier = Modifier
                        .background(
                            color = androidx.compose.ui.graphics.Color.White,
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
                            onClick = { viewModelMap.stormvarselInfoPopUp = false },
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

    Column(
        modifier = Modifier
            .fillMaxWidth(0.16f)
            .wrapContentWidth(Alignment.CenterHorizontally)
            .padding(top = 10.dp)

    ) {
        MenuButton(
            buttonIcon = Icons.Filled.Menu,
            onButtonClicked = { openDrawer() }
        )
    }

    Column(
        modifier = modifier.fillMaxSize()
        ,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        IconButton(
            onClick = { popupControl = true },
            modifier = Modifier
                .padding(start = 0.dp)
        ) {
            Icon(
                Icons.Outlined.Info,
                contentDescription = "Info",
                modifier = Modifier
                    .size(24.dp),
                tint = androidx.compose.ui.graphics.Color.Black
            )
        }
           // Row { // Denne som gjør at pilen er ved siden av tekstgreia
        TextField(
            value = locationSearch.value,
            // onValueChange = viewModelSearch::onSearchChange,
            onValueChange = { newSearchText ->
                viewModelSearch.onSearchChange(newSearchText)
                openSearch = true
            },
            //modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = "Søk på sted") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = true,
            modifier = Modifier.onKeyEvent{
                if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER){
                    Log.d("Knapp", "Trykket Enter")
                    focusManager.clearFocus()
                }
                true
            },
        )
            //}
            // Spacer(modifier = Modifier.height(16.dp))
            //DROPDOWN
        //timeOfDay = chooseTimeOfDay(viewModelSearch)



        if (searchInProgress) {
                Box(/*modifier = Modifier.fillMaxSize()*/) {
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
                        //.weight(1f)
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

                                            // Wait for the cityData list to be populated
                                            while (geoCodeUiState.value.cityList.isEmpty()) {
                                                delay(100) // Wait for 100 milliseconds before checking again
                                            }

                                            cityData = geoCodeUiState.value.cityList


                                            if (cityData.isNotEmpty()) {
                                                userLat = cityData[0].latitude
                                                userLng = cityData[0].longitude
                                                viewModelForecast.updateUserCoord(userLat, userLng)

                                                Log.d("Temp1", cityData[0].name.toString())

                                            } else {
                                                Log.e("Temperatur", "Tom liste")
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
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Spacer(modifier = Modifier.height(0.025 * configuration.screenHeightDp.dp))
            Text(
                text = "Været de neste 24 timene",
                fontWeight = FontWeight.Bold,
                fontSize = 0.05 * configuration.screenWidthDp.sp
            )
            Spacer(modifier = Modifier.height(0.025 * configuration.screenHeightDp.dp))

            //DisplayWeather(temp = temp, windSpeed = windSpeed, windDirection = windDirection, weatherIcon = weatherIcon, location = location)
            Box(
                modifier = Modifier.height(0.3 * configuration.screenHeightDp.dp)
            ){
                LazyRow(
                    Modifier.fillMaxHeight()
                ){
                    var i = 0
                    val timeMap = indexClosestTime(temperatureData)
                    Log.d("WeatherData length", "${temperatureData}")
                    for (key in timeMap.keys) {
                        Log.d("WeatherData length", "${temperatureData[key]}")
                        val temp = temperatureData[key].data.instant.details.air_temperature
                        val windSpeed = temperatureData[key].data.instant.details.wind_speed
                        val windDirection = temperatureData[key].data.instant.details.wind_from_direction
                        val gustSpeed = temperatureData[key].data.instant.details.wind_speed_of_gust
                        val weatherIcon = temperatureData[key].data.next_1_hours.summary.symbol_code
                        val rainAmount = temperatureData[key].data.next_1_hours.details.precipitation_amount
                        val rainProbability = temperatureData[key].data.next_1_hours.details.probability_of_precipitation
                        val lightningProbability = temperatureData[key].data.next_1_hours.details.probability_of_thunder
                        Log.d("WeatherData_Hourly", "time: ${timeMap[key]}, temp: $temp, w.speed: $windSpeed, w.dir.: $windDirection, weatherIcon: $weatherIcon")
                        item {
                            WeatherCard(
                                time = "${timeMap[key]}",
                                temperature = temp,
                                windSpeed = windSpeed,
                                windDirection = windDirection,
                                gustSpeed = gustSpeed,
                                rainAmount = rainAmount,
                                rainProbability = rainProbability,
                                lightningProbability = lightningProbability,
                                weatherIcon = weatherIcon
                            )
                        }
                }
                    //item { WeatherCard(time = "Temperatur", temperature = temp, windSpeed = windSpeed, weatherIcon = weatherIcon) }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
            Box(
                modifier = Modifier.height(0.5 * configuration.screenHeightDp.dp)
            ){
                if (warnings.isNotEmpty()){
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
            }

        }

    }
}



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
}

@Composable
fun WeatherCard(
    time: String,
    temperature: Double,
    windSpeed: Double,
    windDirection: Double,
    gustSpeed: Double,
    rainAmount: Double,
    rainProbability: Double, /* TODO: Remove if not used! */
    lightningProbability: Double, /* TODO: Remove if not used! */
    weatherIcon: String
) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthSp = configuration.screenWidthDp.sp
    val screenHeight = configuration.screenHeightDp.dp
    val icon: Int
    val iconDesc: String

    val cornerShape = 20
    val grayColor = androidx.compose.ui.graphics.Color(0xFFF2F2F2)
    val blackColor = androidx.compose.ui.graphics.Color(0xFF000000)
    val tempColor = when {
        temperature >= 0 -> androidx.compose.ui.graphics.Color(0xFFC91C1C)
        else -> androidx.compose.ui.graphics.Color(0xFF1F39BF)
    }

    when(weatherIcon){
        "clearsky_day" -> {icon = R.drawable.clearsky_day; iconDesc = "Clear sky"}
        "clearsky_night" -> {icon = R.drawable.clearsky_night; iconDesc = "Clear sky"}
        "clearsky_polartwilight" -> {icon = R.drawable.clearsky_polartwilight; iconDesc = "Clear sky"}
        "cloudy" -> {icon = R.drawable.cloudy;iconDesc = "Cloudy"}
        "fair_day" -> {icon = R.drawable.fair_day;iconDesc = "Fair"}
        "fair_night" -> {icon = R.drawable.fair_night;iconDesc = "Fair"}
        "fair_polartwilight" -> {icon = R.drawable.fair_polartwilight;iconDesc = "Fair"}
        "fog" -> {icon = R.drawable.fog ;iconDesc = "Fog"}
        "heavyrain" -> {icon = R.drawable.heavyrain ;iconDesc = "Heavy rain"}
        "heavyrainandthunder" -> {icon = R.drawable.heavyrainandthunder ;iconDesc = "Heavy rain and thunder"}
        "heavyrainshowers_day" -> {icon = R.drawable.heavyrainshowers_day ;iconDesc = "Heavy rain showers"}
        "heavyrainshowers_night" -> {icon = R.drawable.heavyrainshowers_night ;iconDesc = "Heavy rain showers"}
        "heavyrainshowers_polartwilight" -> {icon = R.drawable.heavyrainshowers_polartwilight ;iconDesc = "Heavy rain showers"}
        "heavyrainshowersandthunder_day" -> {icon = R.drawable.heavyrainshowersandthunder_day ;iconDesc = "Heavy rain showers and thunder"}
        "heavyrainshowersandthunder_night" -> {icon = R.drawable.heavyrainshowersandthunder_night ;iconDesc = "Heavy rain showers and thunder"}
        "heavyrainshowersandthunder_polartwilight" -> {icon = R.drawable.heavyrainshowersandthunder_polartwilight ;iconDesc = "Heavy rain showers and thunder"}
        "heavysleet" -> {icon = R.drawable.heavysleet ;iconDesc = "Heavy sleet"}
        "heavysleetandthunder" -> {icon = R.drawable.heavysleetandthunder ;iconDesc = "Heavy sleet and thunder"}
        "heavysleetshowers_day" -> {icon = R.drawable.heavysleetshowers_day ;iconDesc = "Heavy sleet showers"}
        "heavysleetshowers_night" -> {icon = R.drawable.heavysleetshowers_night ;iconDesc = "Heavy sleet showers"}
        "heavysleetshowers_polartwilight" -> {icon = R.drawable.heavysleetshowers_polartwilight ;iconDesc = "Heavy sleet showers"}
        "heavysleetshowersandthunder_day" -> {icon = R.drawable.heavysleetshowersandthunder_day ;iconDesc = "Heavy sleet showers and thunder"}
        "heavysleetshowersandthunder_night" -> {icon = R.drawable.heavysleetshowersandthunder_night ;iconDesc = "Heavy sleet showers and thunder"}
        "heavysleetshowersandthunder_polartwilight" -> {icon = R.drawable.heavysleetshowersandthunder_polartwilight ;iconDesc = "Heavy sleet showers and thunder"}
        "heavysnow" -> {icon = R.drawable.heavysnow ;iconDesc = "Heavy snow"}
        "heavysnowandthunder" -> {icon = R.drawable.heavysnowandthunder ;iconDesc = "Heavy snow and thunder"}
        "heavysnowshowers_day" -> {icon = R.drawable.heavysnowshowers_day ;iconDesc = "Heavy snow showers"}
        "heavysnowshowers_night" -> {icon = R.drawable.heavysnowshowers_night ;iconDesc = "Heavy snow showers"}
        "heavysnowshowers_polartwilight" -> {icon = R.drawable.heavysnowshowers_polartwilight ;iconDesc = "Heavy snow showers"}
        "heavysnowshowersandthunder_day" -> {icon = R.drawable.heavysnowshowersandthunder_day ;iconDesc = "Heavy snow showers and thunder"}
        "heavysnowshowersandthunder_night" -> {icon = R.drawable.heavysnowshowersandthunder_day ;iconDesc = "Heavy snow showers and thunder"}
        "heavysnowshowersandthunder_polartwilight" -> {icon = R.drawable.heavysnowshowersandthunder_day ;iconDesc = "Heavy snow showers and thunder"}
        "lightrain" -> {icon = R.drawable.lightrain ;iconDesc = "Light rain"}
        "lightrainandthunder" -> {icon = R.drawable.lightrainandthunder ;iconDesc = "Light rain and thunder"}
        "lightrainshowers_day" -> {icon = R.drawable.lightrainshowers_day ;iconDesc = "Light rain showers"}
        "lightrainshowers_night" -> {icon = R.drawable.lightrainshowers_night ;iconDesc = "Light rain showers"}
        "lightrainshowers_polartwilight" -> {icon = R.drawable.lightrainshowers_polartwilight ;iconDesc = "Light rain showers"}
        "lightrainshowersandthunder_day" -> {icon = R.drawable.lightrainshowersandthunder_day ;iconDesc = "Light rain showers and thunder"}
        "lightrainshowersandthunder_night" -> {icon = R.drawable.lightrainshowersandthunder_night ;iconDesc = "Light rain showers and thunder"}
        "lightrainshowersandthunder_polartwilight" -> {icon = R.drawable.lightrainshowersandthunder_polartwilight ;iconDesc = "Light rain showers and thunder"}
        "lightsleet" -> {icon = R.drawable.lightsleet ;iconDesc = "Light sleet"}
        "lightsleetandthunder" -> {icon = R.drawable.lightsleetandthunder ;iconDesc = "Light sleet and thunder"}
        "lightsleetshowers_day" -> {icon = R.drawable.lightsleetshowers_day ;iconDesc = "Light sleet showers"}
        "lightsleetshowers_night" -> {icon = R.drawable.lightsleetshowers_night ;iconDesc = "Light sleet showers"}
        "lightsleetshowers_polartwilight" -> {icon = R.drawable.lightsleetshowers_polartwilight ;iconDesc = "Light sleet showers"}
        "lightsnow" -> {icon = R.drawable.lightsnow ;iconDesc = "Light snow"}
        "lightsnowandthunder" -> {icon = R.drawable.lightsnowandthunder ;iconDesc = "Light snow and thunder"}
        "lightsnowshowers_day" -> {icon = R.drawable.lightsnowshowers_day ;iconDesc = "Light snow showers"}
        "lightsnowshowers_night" -> {icon = R.drawable.lightsnowshowers_night ;iconDesc = "Light snow showers"}
        "lightsnowshowers_polartwilight" -> {icon = R.drawable.lightsnowshowers_polartwilight ;iconDesc = "Light snow showers"}
        "lightssleetshowersandthunder_day" -> {icon = R.drawable.lightssleetshowersandthunder_day ;iconDesc = "Light sleet showers and thunder"}
        "lightssleetshowersandthunder_night" -> {icon = R.drawable.lightssleetshowersandthunder_night ;iconDesc = "Light sleet showers and thunder"}
        "lightssleetshowersandthunder_polartwilight" -> {icon = R.drawable.lightssleetshowersandthunder_polartwilight ;iconDesc = "Light sleet showers and thunder"}
        "lightssnowshowersandthunder_day" -> {icon = R.drawable.lightssnowshowersandthunder_day ;iconDesc = "Light snow showers and thunder"}
        "lightssnowshowersandthunder_night" -> {icon = R.drawable.lightssnowshowersandthunder_night ;iconDesc = "Light snow showers and thunder"}
        "lightssnowshowersandthunder_polartwilight" -> {icon = R.drawable.lightssnowshowersandthunder_polartwilight ;iconDesc = "Light snow showers and thunder"}
        "partlycloudy_day" -> {icon = R.drawable.partlycloudy_day ;iconDesc = "Partly cloudy"}
        "partlycloudy_night" -> {icon = R.drawable.partlycloudy_night ;iconDesc = "Partly cloudy"}
        "partlycloudy_polartwilight" -> {icon = R.drawable.partlycloudy_polartwilight ;iconDesc = "Partly cloudy"}
        "rain" -> {icon = R.drawable.rain ;iconDesc = "Rain"}
        "rainandthunder" -> {icon = R.drawable.rainandthunder ;iconDesc = "Rain and thunder"}
        "rainshowers_day" -> {icon = R.drawable.rainshowers_day ;iconDesc = "Rain showers"}
        "rainshowers_night" -> {icon = R.drawable.rainshowers_night ;iconDesc = "Rain showers"}
        "rainshowers_polartwilight" -> {icon = R.drawable.rainshowers_polartwilight ;iconDesc = "Rain showers"}
        "rainshowersandthunder_day" -> {icon = R.drawable.rainshowersandthunder_day ;iconDesc = "Rain showers and thunder"}
        "rainshowersandthunder_night" -> {icon = R.drawable.rainshowersandthunder_night ;iconDesc = "Rain showers and thunder"}
        "rainshowersandthunder_polartwilight" -> {icon = R.drawable.rainshowersandthunder_polartwilight ;iconDesc = "Rain showers and thunder"}
        "sleet" -> {icon = R.drawable.sleet ;iconDesc = "Sleet"}
        "sleetandthunder" -> {icon = R.drawable.sleetandthunder ;iconDesc = "Sleet and thunder"}
        "sleetshowers_day" -> {icon = R.drawable.sleetshowers_day ;iconDesc = "Sleet showers"}
        "sleetshowers_night" -> {icon = R.drawable.sleetshowers_night ;iconDesc = "Sleet showers"}
        "sleetshowers_polartwilight" -> {icon = R.drawable.sleetshowers_polartwilight ;iconDesc = "Sleet showers"}
        "sleetshowersandthunder_day" -> {icon = R.drawable.sleetshowersandthunder_day ;iconDesc = "Sleet showers and thunder"}
        "sleetshowersandthunder_night" -> {icon = R.drawable.sleetshowersandthunder_night ;iconDesc = "Sleet showers and thunder"}
        "sleetshowersandthunder_polartwilight" -> {icon = R.drawable.sleetshowersandthunder_polartwilight ;iconDesc = "Sleet showers and thunder"}
        "snow" -> {icon = R.drawable.snow ;iconDesc = "Snow"}
        "snowandthunder" -> {icon = R.drawable.snowandthunder ;iconDesc = "Snow and thunder"}
        "snowshowers_day" -> {icon = R.drawable.snowshowers_day ;iconDesc = "Snow showers"}
        "snowshowers_night" -> {icon = R.drawable.snowshowers_night ;iconDesc = "Snow showers"}
        "snowshowers_polartwilight" -> {icon = R.drawable.snowshowers_polartwilight ;iconDesc = "Snow showers"}
        "snowshowersandthunder_day" -> {icon = R.drawable.snowshowersandthunder_day ;iconDesc = "Snow showers and thunder"}
        "snowshowersandthunder_night" -> {icon = R.drawable.snowshowersandthunder_night ;iconDesc = "Snow showers and thunder"}
        "snowshowersandthunder_polartwilight" -> {icon = R.drawable.snowshowersandthunder_polartwilight ;iconDesc = "Snow showers and thunder"}
        else -> {icon = R.drawable.round_cloud_sync_24; iconDesc = "Searching for weather"; Log.e("Ikon", "Could not find drawable: $weatherIcon")}
    }

    Box( // Hele boksen
        modifier = Modifier
            .height(0.35 * screenHeight)
            .width(0.8 * screenWidth)
            .padding(
                start = 0.05 * screenWidth,
                end = 0.05 * screenWidth,
                bottom = 0.05 * screenWidth
            )
            .background(
                color = androidx.compose.ui.graphics.Color.White, // Hvit
                shape = RoundedCornerShape(cornerShape.dp)
            )
    ) {
        Box( // Hovedboks
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxHeight(0.9f)
                .fillMaxWidth()
                .background(
                    color = grayColor, // grayColor
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center)
            ){
                Row( // Top row
                    horizontalArrangement = Arrangement.SpaceEvenly
                ){
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = iconDesc,
                        tint = androidx.compose.ui.graphics.Color.Unspecified,
                        modifier = Modifier
                            .size(0.15 * screenWidth)
                    )
                    Text(
                        text = "$temperature C°",
                        fontSize = (0.10 * screenWidthSp),
                        color = tempColor
                    )
                } // End of "Top row"

                Row() { // Bottom row
                    Row( // Wind row
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_air_24),
                            contentDescription = "Wind icon",
                            tint = androidx.compose.ui.graphics.Color.Unspecified,
                            modifier = Modifier
                                .size(0.06 * screenWidth)
                        )
                        Text(
                            text = "$windSpeed",
                            fontSize = (0.07 * screenWidthSp),
                            color = blackColor
                        )
                        Column(
                            modifier = Modifier
                                .align(Top)
                        ){
                            Image(
                                painter = painterResource(id = R.drawable.baseline_arrow_right_alt_24),
                                contentDescription = "Wind arrow",
                                modifier = Modifier
                                    .wrapContentSize()
                                    .size(0.05 * screenWidth)
                                    .graphicsLayer(
                                        rotationZ = windDirection.toFloat()
                                    )
                            )
                            Text(
                                text = "($gustSpeed)",
                                fontSize = (0.03 * screenWidthSp),
                                color = blackColor
                            )
                        }

                    } // End of "Wind row"
                    Row( // Rain row
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.water_drop_24),
                            contentDescription = "Rain icon",
                            tint = androidx.compose.ui.graphics.Color.Unspecified,
                            modifier = Modifier
                                .size(0.07 * screenWidth)
                        )
                        Text(
                            text = "$rainAmount",
                            fontSize = (0.07 * screenWidthSp),
                            color = blackColor,
                            modifier = Modifier.align(Bottom)
                        )
                        Text(
                            text = "mm",
                            fontSize = (0.03 * screenWidthSp),
                            color = blackColor,
                            modifier = Modifier.align(Bottom)
                        )

                    } // End of "Rain row"
                } // End of "Bottom row"
            }
        } // Hovedboks slutt

        val hour = time.removeSuffix("Z").split("T")[1].toString().split(":")
        val useTime = "${hour[0]}:${hour[1]}"

        Box( // Overskrift
            modifier = Modifier
                .align(Alignment.TopCenter)
                .wrapContentSize(Alignment.Center)
                .fillMaxHeight(0.2f)
                .fillMaxWidth(0.5f)
                .background(
                    color = grayColor,
                    shape = RoundedCornerShape(10.dp)
                )
                .border(
                    border = BorderStroke(1.dp, androidx.compose.ui.graphics.Color.Black),
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
            Text(
                text = useTime,
                modifier = Modifier
                    .wrapContentSize(Alignment.Center)
                    .align(Alignment.Center)
                    .background(grayColor),
                fontWeight = FontWeight.Bold
            )
        }// Overskrift slutt
    }
}

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

@RequiresApi(Build.VERSION_CODES.O)
fun createInstant(date: String): Instant? {
    val currentTimeData = date.removeSuffix("Z").split("T")
    val currentYear = currentTimeData[0].split("-")[0].toInt()
    val currentMonth = currentTimeData[0].split("-")[1].toInt()
    val currentDay = currentTimeData[0].split("-")[2].toInt()
    val currentHour = currentTimeData[1].split(":")[0].toInt()
    val currentMinute = currentTimeData[1].split(":")[1].toInt()
    val currentSecond = currentTimeData[1].split(":")[2].toInt()
    val currentInstant = Instant.ofEpochSecond(0)
        .atZone(ZoneOffset.UTC)
        .withYear(currentYear)
        .withMonth(currentMonth)
        .withDayOfMonth(currentDay)
        .withHour(currentHour)
        .withMinute(currentMinute)
        .withSecond(currentSecond)
        .toInstant()
    return currentInstant
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
    var i = 0
    for (item in listOfTime) {
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
        i++
    }
    return returnMap
}

@RequiresApi(Build.VERSION_CODES.O)
fun compareTimes(currentInstant: Instant, checkTimeInstant: Instant): Long {
// find the difference in seconds
    val diffSeconds = ChronoUnit.SECONDS.between(currentInstant, checkTimeInstant)
    return diffSeconds
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
private fun locationToLatLng(loc: Location?): LatLng {
    return LatLng(loc!!.latitude, loc.longitude)
}
fun getColor(awarenessLevel: String): String {
    val color = awarenessLevel.split("; ")[1]
    Log.d("Farge", color)

    return when (color) {
        "green" -> "#803AF93C"
        "yellow" -> "#80F5D062"
        "orange" -> "#80F78D02"
        "red" -> "#80F93C3A"
        else -> "#40000000"
    }
}
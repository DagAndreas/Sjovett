package com.in2000_project.BoatApp.ui.screens

//package com.example.StormWarning

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import kotlinx.coroutines.launch
import android.graphics.Color
import android.location.Location
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.CameraPosition
import com.in2000_project.BoatApp.MenuButton
import java.util.*


// latitude = north-south
var userLat = 59.911 // disse skal endres til brukerens faktiske lokasjon
// longitude = east-west
var userLng = 10.757


@Composable
fun StormWarning(
    viewModelAlerts: MetAlertsViewModel,
    viewModelForecast: LocationForecastViewModel,
    viewModelMap: AlertsMapViewModel,
    setupClusterManager: (Context, GoogleMap) -> ZoneClusterManager,
    calculateZoneViewCenter: () -> LatLngBounds,
    modifier: Modifier,
    openDrawer: () -> Unit
){
    // hentet fra MapScreen:
    val mapState by viewModelMap.state.collectAsState()
    val mapProperties = MapProperties(
        // Only enable if user has accepted location permissions.
        isMyLocationEnabled = mapState.lastKnownLocation != null, // denne er null - finner brukerens posisjon dersom den settes til true
    )

    Log.d("tester", mapState.lastKnownLocation.toString())

    val cameraPositionState = rememberCameraPositionState{
        position = CameraPosition.fromLatLngZoom(LatLng(65.0, 14.0), 4f)
    }
    //slutt på hentet fra MapScreen
    // nullpointerException
    // var myPosition by remember { mutableStateOf(locationToLatLng(mapState.lastKnownLocation)!!) }


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
    var weatherIcon = ""
    if (temperatureData != emptyList<Timesery>()) {
        temp = temperatureData[0].data.instant.details.air_temperature
        windSpeed = temperatureData[0].data.instant.details.wind_speed
        windDirection = 90.0 + temperatureData[0].data.instant.details.wind_from_direction
        weatherIcon = temperatureData[0].data.next_1_hours.summary.symbol_code
        Log.d("WindDir", "${windDirection-90}")
        Log.d("truls", temperatureData[0].data.instant.details.air_temperature.toString())
    }
    // Therese slutt
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

        MenuButton(
            buttonIcon = Icons.Filled.Menu,
            onButtonClicked = { openDrawer() }
        )

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
            DisplayWeather(temp = temp, windSpeed = windSpeed, windDirection = windDirection, weatherIcon = weatherIcon)
            Spacer(modifier = Modifier.height(30.dp))
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
private fun locationToLatLng(loc: Location?): LatLng {
    return LatLng(loc!!.latitude, loc.longitude)
}
fun getColor(awarenessLevel: String): String {
    val color = awarenessLevel.split("; ")[1]
    Log.d("Farge", color)

    return when (color) {
        "green" -> "#803AF93C"
        "yellow" -> "#80F5D062"
        "red" -> "#80F93C3A"
        else -> "#40000000"
    }
}

@Composable
fun DisplayWeather(
    modifier: Modifier = Modifier
        .fillMaxSize()
        .wrapContentSize(Alignment.Center),
    temp: Double,
    windSpeed: Double,
    windDirection: Double,
    weatherIcon: String
) {

    Row(
        modifier = Modifier
            .border(
                BorderStroke(2.dp, androidx.compose.ui.graphics.Color.Black),
                shape = RoundedCornerShape(15.dp)
            )
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly



    ){
        // Her velges ikon
        Log.d("Ikon", weatherIcon)
        val icon: Int
        val icon_desc: String
        when(weatherIcon){
            "clearsky_day" -> {icon = R.drawable.clearsky_day; icon_desc = "Clear sky"}
            "clearsky_night" -> {icon = R.drawable.clearsky_night; icon_desc = "Clear sky"}
            "clearsky_polartwilight" -> {icon = R.drawable.clearsky_polartwilight; icon_desc = "Clear sky"}
            "cloudy" -> {icon = R.drawable.cloudy;icon_desc = "Cloudy"}
            "fair_day" -> {icon = R.drawable.fair_day;icon_desc = "Fair"}
            "fair_night" -> {icon = R.drawable.fair_night;icon_desc = "Fair"}
            "fair_polartwilight" -> {icon = R.drawable.fair_polartwilight;icon_desc = "Fair"}
            "fog" -> {icon = R.drawable.fog ;icon_desc = "Fog"}
            "heavyrain" -> {icon = R.drawable.heavyrain ;icon_desc = "Heavy rain"}
            "heavyrainandthunder" -> {icon = R.drawable.heavyrainandthunder ;icon_desc = "Heavy rain and thunder"}
            "heavyrainshowers_day" -> {icon = R.drawable.heavyrainshowers_day ;icon_desc = "Heavy rain showers"}
            "heavyrainshowers_night" -> {icon = R.drawable.heavyrainshowers_night ;icon_desc = "Heavy rain showers"}
            "heavyrainshowers_polartwilight" -> {icon = R.drawable.heavyrainshowers_polartwilight ;icon_desc = "Heavy rain showers"}
            "heavyrainshowersandthunder_day" -> {icon = R.drawable.heavyrainshowersandthunder_day ;icon_desc = "Heavy rain showers and thunder"}
            "heavyrainshowersandthunder_night" -> {icon = R.drawable.heavyrainshowersandthunder_night ;icon_desc = "Heavy rain showers and thunder"}
            "heavyrainshowersandthunder_polartwilight" -> {icon = R.drawable.heavyrainshowersandthunder_polartwilight ;icon_desc = "Heavy rain showers and thunder"}
            "heavysleet" -> {icon = R.drawable.heavysleet ;icon_desc = "Heavy sleet"}
            "heavysleetandthunder" -> {icon = R.drawable.heavysleetandthunder ;icon_desc = "Heavy sleet and thunder"}
            "heavysleetshowers_day" -> {icon = R.drawable.heavysleetshowers_day ;icon_desc = "Heavy sleet showers"}
            "heavysleetshowers_night" -> {icon = R.drawable.heavysleetshowers_night ;icon_desc = "Heavy sleet showers"}
            "heavysleetshowers_polartwilight" -> {icon = R.drawable.heavysleetshowers_polartwilight ;icon_desc = "Heavy sleet showers"}
            "heavysleetshowersandthunder_day" -> {icon = R.drawable.heavysleetshowersandthunder_day ;icon_desc = "Heavy sleet showers and thunder"}
            "heavysleetshowersandthunder_night" -> {icon = R.drawable.heavysleetshowersandthunder_night ;icon_desc = "Heavy sleet showers and thunder"}
            "heavysleetshowersandthunder_polartwilight" -> {icon = R.drawable.heavysleetshowersandthunder_polartwilight ;icon_desc = "Heavy sleet showers and thunder"}
            "heavysnow" -> {icon = R.drawable.heavysnow ;icon_desc = "Heavy snow"}
            "heavysnowandthunder" -> {icon = R.drawable.heavysnowandthunder ;icon_desc = "Heavy snow and thunder"}
            "heavysnowshowers_day" -> {icon = R.drawable.heavysnowshowers_day ;icon_desc = "Heavy snow showers"}
            "heavysnowshowers_night" -> {icon = R.drawable.heavysnowshowers_night ;icon_desc = "Heavy snow showers"}
            "heavysnowshowers_polartwilight" -> {icon = R.drawable.heavysnowshowers_polartwilight ;icon_desc = "Heavy snow showers"}
            "heavysnowshowersandthunder_day" -> {icon = R.drawable.heavysnowshowersandthunder_day ;icon_desc = "Heavy snow showers and thunder"}
            "heavysnowshowersandthunder_night" -> {icon = R.drawable.heavysnowshowersandthunder_day ;icon_desc = "Heavy snow showers and thunder"}
            "heavysnowshowersandthunder_polartwilight" -> {icon = R.drawable.heavysnowshowersandthunder_day ;icon_desc = "Heavy snow showers and thunder"}
            "lightrain" -> {icon = R.drawable.lightrain ;icon_desc = "Light rain"}
            "lightrainandthunder" -> {icon = R.drawable.lightrainandthunder ;icon_desc = "Light rain and thunder"}
            "lightrainshowers_day" -> {icon = R.drawable.lightrainshowers_day ;icon_desc = "Light rain showers"}
            "lightrainshowers_night" -> {icon = R.drawable.lightrainshowers_night ;icon_desc = "Light rain showers"}
            "lightrainshowers_polartwilight" -> {icon = R.drawable.lightrainshowers_polartwilight ;icon_desc = "Light rain showers"}
            "lightrainshowersandthunder_day" -> {icon = R.drawable.lightrainshowersandthunder_day ;icon_desc = "Light rain showers and thunder"}
            "lightrainshowersandthunder_night" -> {icon = R.drawable.lightrainshowersandthunder_night ;icon_desc = "Light rain showers and thunder"}
            "lightrainshowersandthunder_polartwilight" -> {icon = R.drawable.lightrainshowersandthunder_polartwilight ;icon_desc = "Light rain showers and thunder"}
            "lightsleet" -> {icon = R.drawable.lightsleet ;icon_desc = "Light sleet"}
            "lightsleetandthunder" -> {icon = R.drawable.lightsleetandthunder ;icon_desc = "Light sleet and thunder"}
            "lightsleetshowers_day" -> {icon = R.drawable.lightsleetshowers_day ;icon_desc = "Light sleet showers"}
            "lightsleetshowers_night" -> {icon = R.drawable.lightsleetshowers_night ;icon_desc = "Light sleet showers"}
            "lightsleetshowers_polartwilight" -> {icon = R.drawable.lightsleetshowers_polartwilight ;icon_desc = "Light sleet showers"}
            "lightsnow" -> {icon = R.drawable.lightsnow ;icon_desc = "Light snow"}
            "lightsnowandthunder" -> {icon = R.drawable.lightsnowandthunder ;icon_desc = "Light snow and thunder"}
            "lightsnowshowers_day" -> {icon = R.drawable.lightsnowshowers_day ;icon_desc = "Light snow showers"}
            "lightsnowshowers_night" -> {icon = R.drawable.lightsnowshowers_night ;icon_desc = "Light snow showers"}
            "lightsnowshowers_polartwilight" -> {icon = R.drawable.lightsnowshowers_polartwilight ;icon_desc = "Light snow showers"}
            "lightssleetshowersandthunder_day" -> {icon = R.drawable.lightssleetshowersandthunder_day ;icon_desc = "Light sleet showers and thunder"}
            "lightssleetshowersandthunder_night" -> {icon = R.drawable.lightssleetshowersandthunder_night ;icon_desc = "Light sleet showers and thunder"}
            "lightssleetshowersandthunder_polartwilight" -> {icon = R.drawable.lightssleetshowersandthunder_polartwilight ;icon_desc = "Light sleet showers and thunder"}
            "lightssnowshowersandthunder_day" -> {icon = R.drawable.lightssnowshowersandthunder_day ;icon_desc = "Light snow showers and thunder"}
            "lightssnowshowersandthunder_night" -> {icon = R.drawable.lightssnowshowersandthunder_night ;icon_desc = "Light snow showers and thunder"}
            "lightssnowshowersandthunder_polartwilight" -> {icon = R.drawable.lightssnowshowersandthunder_polartwilight ;icon_desc = "Light snow showers and thunder"}
            "partlycloudy_day" -> {icon = R.drawable.partlycloudy_day ;icon_desc = "Partly cloudy"}
            "partlycloudy_night" -> {icon = R.drawable.partlycloudy_night ;icon_desc = "Partly cloudy"}
            "partlycloudy_polartwilight" -> {icon = R.drawable.partlycloudy_polartwilight ;icon_desc = "Partly cloudy"}
            "rain" -> {icon = R.drawable.rain ;icon_desc = "Rain"}
            "rainandthunder" -> {icon = R.drawable.rainandthunder ;icon_desc = "Rain and thunder"}
            "rainshowers_day" -> {icon = R.drawable.rainshowers_day ;icon_desc = "Rain showers"}
            "rainshowers_night" -> {icon = R.drawable.rainshowers_night ;icon_desc = "Rain showers"}
            "rainshowers_polartwilight" -> {icon = R.drawable.rainshowers_polartwilight ;icon_desc = "Rain showers"}
            "rainshowersandthunder_day" -> {icon = R.drawable.rainshowersandthunder_day ;icon_desc = "Rain showers and thunder"}
            "rainshowersandthunder_night" -> {icon = R.drawable.rainshowersandthunder_night ;icon_desc = "Rain showers and thunder"}
            "rainshowersandthunder_polartwilight" -> {icon = R.drawable.rainshowersandthunder_polartwilight ;icon_desc = "Rain showers and thunder"}
            "sleet" -> {icon = R.drawable.sleet ;icon_desc = "Sleet"}
            "sleetandthunder" -> {icon = R.drawable.sleetandthunder ;icon_desc = "Sleet and thunder"}
            "sleetshowers_day" -> {icon = R.drawable.sleetshowers_day ;icon_desc = "Sleet showers"}
            "sleetshowers_night" -> {icon = R.drawable.sleetshowers_night ;icon_desc = "Sleet showers"}
            "sleetshowers_polartwilight" -> {icon = R.drawable.sleetshowers_polartwilight ;icon_desc = "Sleet showers"}
            "sleetshowersandthunder_day" -> {icon = R.drawable.sleetshowersandthunder_day ;icon_desc = "Sleet showers and thunder"}
            "sleetshowersandthunder_night" -> {icon = R.drawable.sleetshowersandthunder_night ;icon_desc = "Sleet showers and thunder"}
            "sleetshowersandthunder_polartwilight" -> {icon = R.drawable.sleetshowersandthunder_polartwilight ;icon_desc = "Sleet showers and thunder"}
            "snow" -> {icon = R.drawable.snow ;icon_desc = "Snow"}
            "snowandthunder" -> {icon = R.drawable.snowandthunder ;icon_desc = "Snow and thunder"}
            "snowshowers_day" -> {icon = R.drawable.snowshowers_day ;icon_desc = "Snow showers"}
            "snowshowers_night" -> {icon = R.drawable.snowshowers_night ;icon_desc = "Snow showers"}
            "snowshowers_polartwilight" -> {icon = R.drawable.snowshowers_polartwilight ;icon_desc = "Snow showers"}
            "snowshowersandthunder_day" -> {icon = R.drawable.snowshowersandthunder_day ;icon_desc = "Snow showers and thunder"}
            "snowshowersandthunder_night" -> {icon = R.drawable.snowshowersandthunder_night ;icon_desc = "Snow showers and thunder"}
            "snowshowersandthunder_polartwilight" -> {icon = R.drawable.snowshowersandthunder_polartwilight ;icon_desc = "Snow showers and thunder"}
            else -> {icon = R.drawable.unsure; icon_desc = "0"; Log.e("Ikon", "Could not find drawable: $weatherIcon")}
        }

        Image(
            painter = painterResource(id = icon),
            contentDescription = icon_desc,
            modifier = Modifier
                .wrapContentSize()
                .size(75.dp)
                .fillMaxHeight()

        )
        //Spacer(modifier = Modifier.width(20.dp))

        androidx.compose.material.Text(
            "$temp C°",
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold
        )
        //Spacer(modifier = Modifier.width(10.dp))
        androidx.compose.material.Text(
            "$windSpeed m/s",
            fontSize = 25.sp,
            fontWeight = FontWeight.Light
        )
        Column() {
            Image(
                painter = painterResource(id = R.drawable.baseline_arrow_right_alt_24),
                contentDescription = "Wind arrow",
                modifier = Modifier
                    .wrapContentSize()
                    .size(35.dp)
                    .graphicsLayer(
                        rotationZ = windDirection.toFloat()
                    )

            )
        }
    }
}//DisplayWeather
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
package com.in2000_project.BoatApp.viewmodel

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.in2000_project.BoatApp.maps.*
import com.in2000_project.BoatApp.data.MapState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.in2000_project.BoatApp.maps.CircleInfo
import com.in2000_project.BoatApp.model.oceanforecast.Details
import com.in2000_project.BoatApp.model.oceanforecast.Timesery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@HiltViewModel
class MapViewModel @Inject constructor(): ViewModel() {

    lateinit var locationProviderClient: FusedLocationProviderClient
    fun setClient(fusedLocationProviderClient: FusedLocationProviderClient) {
        locationProviderClient = fusedLocationProviderClient
    }
    private val _state = MutableStateFlow(
        MapState(
            lastKnownLocation = null,
            circle = CircleInfo(
                coordinates = LatLng(59.0373, 10.5883), //default 59, 10.5. Oslofjorden.
                radius = 25.0
            )
        )
    )

    val state: StateFlow<MapState> = _state.asStateFlow()

    var displayedText = mutableStateOf("Du kan legge til en destinasjon ved å holde inne et sted på kartet. ")


    //Hvem er var og val egentlig? Mutablestate gjør det mulig å endre på pekerens
    var distanceInMeters = mutableStateOf(0.0)
    var lengthInMinutes = mutableStateOf(0.0)
    var polyLines =  mutableStateListOf<PolylineOptions>()
    var lockMarkers =  mutableStateOf(false)
    var usingMyPositionTidsbruk = mutableStateOf(false)


    var markerPositions =  mutableStateListOf<LatLng>()
    //.apply { myPosition.value?.let { add(it) } }
    var speedNumber =    mutableStateOf(15f)
    // distance between all of the markers
    var coordinatesToFindDistanceBetween = mutableStateListOf<LatLng>()
    val markersMapScreen = mutableListOf<LatLng>()
    val polyLinesMap = mutableListOf<PolylineOptions>()
    var circleCenter = mutableStateOf(state.value.circle.coordinates)
    var circleRadius = mutableStateOf(25.0)
    var circleVisibility = mutableStateOf(false)
    var enabled = mutableStateOf(true) //shift-f6 til "sosButtonVisible
    var timePassedInSeconds = mutableStateOf( 0 )
    var mann_er_overbord = mutableStateOf(false)

    //InfoKort
    var mannOverBordInfoPopUp by mutableStateOf(true)
    var reiseplanleggerInfoPopUp by mutableStateOf(true)

    var infoTextMannOverBord by mutableStateOf("test")


    val a = Log.d("Oppretter ovm", "$oceanURL?lat=${circleCenter.value.latitude}&lon=${circleCenter.value.longitude}")
    val oceanViewModel = OceanViewModel("$oceanURL?lat=${circleCenter.value.latitude}&lon=${circleCenter.value.longitude}")


    var mapUpdateThread = MapUpdateThread(this)
    class MapUpdateThread(
        val mapViewModel: MapViewModel
    ) : Thread() {
        var isRunning = false
        override fun run() {
            sleep(100) //sov litt for å ha tid til å hente oceanforecastobject
            isRunning = true
            Log.i("Hei", "fra tråd")
            val sleep_delay:Long = 2 //sekunder
            while(isRunning){
                Log.i("Hei", "fra trådloop")
                mapViewModel.updateMap(sleep_delay)
                mapViewModel.updateMarkerAndPolyLines()
                sleep(sleep_delay*1000) // x antall sek
            }
        }
    }


    fun restartButton(){
        mapUpdateThread.isRunning = false
        circleCenter.value = state.value.circle.coordinates
        circleRadius.value = 25.0
        circleVisibility.value = false
        enabled.value = true
        timePassedInSeconds.value =  0
        mann_er_overbord.value = false
        polyLinesMap.clear()
    }

    fun startButton(state: Location?, pos: LatLng){
        circleCenter.value = locationToLatLng(state)
        oceanViewModel.setPath(circleCenter.value)
        oceanViewModel.getOceanForecastResponse()
        circleVisibility.value = true
        enabled.value = true
        mann_er_overbord.value = true
        markersMapScreen.add(pos)
        mapUpdateThread.isRunning = true
        mapUpdateThread = MapUpdateThread(this)
        mapUpdateThread.start()
    }

    fun updateMap(waittime: Long){
        timePassedInSeconds.value += waittime.toInt()
        circleCenter.value = calculateNewPosition(circleCenter.value, oceanViewModel, waittime.toDouble()/60.0)
        circleRadius.value = calculateRadius(timePassedInSeconds.value/60)
    }

    fun updateMarkerAndPolyLines(){
        markersMapScreen.add(circleCenter.value)
        if(markersMapScreen.size>1){
            val lastPosition = markersMapScreen[markersMapScreen.size - 2]
            val options = PolylineOptions()
                .add(lastPosition, markersMapScreen.last())
                .color(android.graphics.Color.BLACK)
            polyLinesMap.add(options)
        }
    }

    fun updateLocation() {
        try {
            val locationResult = locationProviderClient.lastLocation
            locationResult.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val location = task.result
                    if (location != null) {
                        _state.update {
                            it.copy(
                                lastKnownLocation = location,
                            )
                        }
                    }
                }
            }
        } catch (e: SecurityException) {
            // Show error or something
        }
    }

    @SuppressLint("MissingPermission")
    fun getDeviceLocation(
        fusedLocationProviderClient: FusedLocationProviderClient
    ) {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _state.value = state.value.copy(
                        lastKnownLocation = task.result,
                    )
                }
            }
        } catch (e: SecurityException) {
            // Show error or something
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

    Log.i("MapScreen Bølge", "seawaterspeed: ${forecastDetails.sea_water_speed}, seawaterdirection: ${forecastDetails.sea_water_to_direction}")
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








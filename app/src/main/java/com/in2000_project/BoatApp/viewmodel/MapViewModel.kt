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
import com.in2000_project.BoatApp.view.screens.calculateDistance
import com.in2000_project.BoatApp.view.screens.calculateTimeInMinutes
import com.in2000_project.BoatApp.view.screens.formatTime
import com.in2000_project.BoatApp.data.CircleState
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
import kotlin.math.*
import kotlin.system.exitProcess

@HiltViewModel
class MapViewModel @Inject constructor(): ViewModel() {

    lateinit var locationProviderClient: FusedLocationProviderClient
    fun setClient(fusedLocationProviderClient: FusedLocationProviderClient) {
        locationProviderClient = fusedLocationProviderClient
    }
    private val _state = MutableStateFlow(
        MapState(
            lastKnownLocation = null,
            circle = CircleState(
                coordinates = LatLng(59.0373, 10.5883), //default 59, 10.5. Oslofjorden.
                radius = 25.0
            )
        )
    )

    val state: StateFlow<MapState> = _state.asStateFlow()
    var displayedText = mutableStateOf("Du kan legge til en destinasjon ved å holde inne et sted på kartet. ")

    // These are for Reiseplanlegger
    var distanceInMeters = mutableStateOf(0.0)
    var lengthInMinutes = mutableStateOf(0.0)
    var polyLines =  mutableStateListOf<PolylineOptions>()
    var lockMarkers =  mutableStateOf(false)
    var usingMyPositionTidsbruk = mutableStateOf(false)
    var markerPositions = mutableStateListOf<LatLng>()
    var speedNumber = mutableStateOf(15f)
    var coordinatesToFindDistanceBetween = mutableStateListOf<LatLng>()

    // These are for Mann-over-bord
    val markersMapScreen = mutableListOf<LatLng>()
    val polyLinesMap = mutableListOf<PolylineOptions>()
    var circleCenter = mutableStateOf(state.value.circle.coordinates)
    var circleRadius = mutableStateOf(25.0)
    var circleVisibility = mutableStateOf(false)
    var enabled = mutableStateOf(true)
    var timePassedInSeconds = mutableStateOf( 0 )
    var manIsOverboard = mutableStateOf(false)
    var buttonText = "Start søk"

    // PopUp
    var manIsOverboardInfoPopup by mutableStateOf(true)
    var reiseplanleggerInfoPopup by mutableStateOf(true)

    //var infoTextMannOverBord by mutableStateOf("")
    //var infoTextReiseplanlegger by mutableStateOf("Hold inne på kartet for å legge til markører. Sveip opp for å planlegge reisen.\n" +
            //"NB! Denne reiseplanleggeren tar ikke hensyn til skjær og grunner. Rute planlegges på eget ansvar.")

    var showDialog by mutableStateOf(false)

    val oceanViewModel = OceanViewModel("$oceanURL?lat=${circleCenter.value.latitude}&lon=${circleCenter.value.longitude}")

    var mapUpdateThread = MapUpdateThread(this)
    class MapUpdateThread(
        val mapViewModel: MapViewModel
    ) : Thread() {
        var isRunning = false
        override fun run() {
            mapViewModel.oceanViewModel.setPath(mapViewModel.circleCenter.value)
            mapViewModel.oceanViewModel.getOceanForecastResponse()
            sleep(400) // Sleeps to ensure that data has been collected from oceanforecastobject
            isRunning = true
            val sleepDelay:Long = 2 // seconds
            while(isRunning){
                // sleepDelay counts the seconds between updates, sleepDelay*30 will simulate 60 seconds every 2 seconds
                mapViewModel.updateMap(sleepDelay*200)
                mapViewModel.updateMarkerAndPolyLines()
                // in milliseconds, this function waits 2 seconds between each update
                sleep(sleepDelay*50)
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
        manIsOverboard.value = false
        polyLinesMap.clear()
    }

    fun startButton(state: Location?, pos: LatLng){
        circleCenter.value = locationToLatLng(state)
        oceanViewModel.setPath(circleCenter.value)
        oceanViewModel.getOceanForecastResponse()
        circleVisibility.value = true
        enabled.value = true
        manIsOverboard.value = true
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
            Log.e("updateLocation", e.toString())
            exitProcess(-1)
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
            Log.e("getDeviceLocation", e.toString())
        }
    }

    fun removeLastMarker() {
        if (markerPositions.size >= 2) {
            // Remove the last marker position
            markerPositions.removeLast()
            coordinatesToFindDistanceBetween.removeLast()

            // Remove the last polyline and update the distance and time
            polyLines.removeLast()

            if (coordinatesToFindDistanceBetween.size > 1) {
                distanceInMeters.value = calculateDistance(coordinatesToFindDistanceBetween)
                lengthInMinutes.value = calculateTimeInMinutes(distanceInMeters.value, speedNumber.value)
            }
        } else if (markerPositions.size == 1) {
            // If there is only one marker position left, remove it and update the displayed text
            if(!usingMyPositionTidsbruk.value) {
                markerPositions.removeLast()
                coordinatesToFindDistanceBetween.removeLast()
            }
        }
        updateDisplayedText()
    }

    // Updates the displayed text for the user
    fun updateDisplayedText() {
        if (speedNumber.value == 0f) {
            displayedText.value = "Du vil ikke komme fram hvis du kjører 0 knop"

        } else {
            if (markerPositions.size < 2) {
                displayedText.value = "Du kan legge til en destinasjon ved å holde inne et sted på kartet. "
            } else {
                displayedText.value = formatTime(lengthInMinutes.value)
            }
        }
    }
    
    fun updateUseOfCurrentLocation(state: MapState) {
        usingMyPositionTidsbruk.value = !usingMyPositionTidsbruk.value

        if(!usingMyPositionTidsbruk.value){
            markerPositions.removeFirst()
            coordinatesToFindDistanceBetween.removeFirst()

            if (polyLines.isNotEmpty()) {
                polyLines.removeFirst()
            }
        }
        else{
            markerPositions.add(0, locationToLatLng(state.lastKnownLocation))
            coordinatesToFindDistanceBetween.add(0,markerPositions[0])
            if (coordinatesToFindDistanceBetween.size >=2){
                val polyLine = PolylineOptions().add(markerPositions[0], markerPositions[1]).color(android.graphics.Color.BLACK)
                polyLines.add(0, polyLine)
            }
        }
        distanceInMeters.value = calculateDistance(coordinatesToFindDistanceBetween)
        lengthInMinutes.value = calculateTimeInMinutes(distanceInMeters.value, speedNumber.value)

        updateDisplayedText()
    }
}

/* TODO: Når det hentes ny oceanforecdast, så må det sjekkes om det er en null, før den assignes på nytt. */
fun calculateNewPosition(personCoordinate: LatLng, ovm: OceanViewModel, time: Double): LatLng{
    Log.i("MapScreen", "New Pos from $personCoordinate")
    val dataCoordinate = ovm.oceanForecastResponseObject.geometry.coordinates
    val dataLatLng = LatLng(dataCoordinate[1], dataCoordinate[0])

    if (hasChangedGrid(dataLatLng, personCoordinate)){
        ovm.setPath(personCoordinate)
        ovm.getOceanForecastResponse()
    }
    //Finds the Timesery (object with oceandata) that is closest timestamp
    val forecastDetails = findClosestDataToTimestamp(ovm.oceanForecastResponseObject.properties.timeseries)

    Log.i("MapScreen Wave", "seawaterspeed: ${forecastDetails.sea_water_speed}, seawaterdirection: ${forecastDetails.sea_water_to_direction}")
    return calculatePosition(listOf(personCoordinate.latitude, personCoordinate.longitude), forecastDetails.sea_surface_wave_from_direction, forecastDetails.sea_water_speed, time)
}

/** henter den listen med bølgedata som er nærmest nåværende klokkeslett */
@SuppressLint("SimpleDateFormat")
fun findClosestDataToTimestamp(listOfTime: List<Timesery>): Details {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    val currentTime = Date()
    var i = 0
    var smallestIndex = 0
    var smallestSecondsBetween = Long.MAX_VALUE
    for (item in listOfTime) {
        val checkTime: Date
        try {
            checkTime = sdf.parse(item.time) as Date
        } catch (e: ParseException) {
            e.printStackTrace()
            continue
        }

        val secondsBetween = getSecondsBetween(currentTime, checkTime)
        if (secondsBetween in 0 until smallestSecondsBetween) {
            smallestIndex = i
            smallestSecondsBetween = secondsBetween
        }
        i++
    }

    return listOfTime[smallestIndex].data.instant.details
}
fun getSecondsBetween(date1: Date, date2: Date): Long {
    val diffInMilliseconds = abs(date1.time - date2.time)
    return TimeUnit.MILLISECONDS.toSeconds(diffInMilliseconds)
}

/** brukes for å hente posisjonen fra state. default hvis null*/
fun locationToLatLng(loc: Location?): LatLng {
    if (loc != null){ return LatLng(loc.latitude, loc.longitude)}
    Log.i("locationToLatLng","Fant ingen location. Returnerer default LatLng(59.0, 11.0)")
    return LatLng(59.0, 11.0) //default val i oslofjorden
}

// Take into account that we assume timeCheckingFor is given in minutes
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

// Calculates the radius of the search-area
fun calculateRadius(minutes: Int): Double {
    val newRadius: Double = minutes * 5.0
    return if (newRadius > 200.0) 200.0
    else if (newRadius < 25.0) 25.0
    else newRadius
}










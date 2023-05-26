package com.in2000_project.BoatApp.viewmodel

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.in2000_project.BoatApp.data.CircleState
import com.in2000_project.BoatApp.data.MapState
import com.in2000_project.BoatApp.launch.CheckInternet
import com.in2000_project.BoatApp.launch.InternetPopupState
import com.in2000_project.BoatApp.maps.*
import com.in2000_project.BoatApp.model.oceanforecast.Details
import com.in2000_project.BoatApp.model.oceanforecast.Timesery
import com.in2000_project.BoatApp.view.components.mann_over_bord.MapUpdateThread
import com.in2000_project.BoatApp.view.screens.calculateDistance
import com.in2000_project.BoatApp.view.screens.calculateTimeInMinutes
import com.in2000_project.BoatApp.view.screens.formatTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.*
import kotlin.system.exitProcess

@HiltViewModel
class MapViewModel @Inject constructor() : ViewModel() {

    private lateinit var locationProviderClient: FusedLocationProviderClient
    fun setClient(fusedLocationProviderClient: FusedLocationProviderClient) {
        locationProviderClient = fusedLocationProviderClient
    }

    private val _state = MutableStateFlow(
        MapState(
            lastKnownLocation = null, circle = CircleState(
                coordinates = LatLng(59.0373, 10.5883), //default 59, 10.5. Oslofjorden.
                radius = 25.0
            )
        )
    )

    val state: StateFlow<MapState> = _state.asStateFlow()
    var displayedText =
        mutableStateOf("Du kan legge til en destinasjon ved å holde inne et sted på kartet. ")

    // These are for Reiseplanlegger
    var distanceInMeters = mutableStateOf(0.0)
    var lengthInMinutes = mutableStateOf(0.0)
    var polyLines = mutableStateListOf<PolylineOptions>()
    var lockMarkers = mutableStateOf(false)
    var usingMyPositionTidsbruk = mutableStateOf(false)
    var markerPositions = mutableStateListOf<LatLng>()
    var speedNumber = mutableStateOf(15f)
    var coordinatesToFindDistanceBetween = mutableStateListOf<LatLng>()

    // These are for Mann-over-bord
    private val markersMapScreen = mutableListOf<LatLng>()
    val polyLinesMap = mutableListOf<PolylineOptions>()
    var circleCenter = mutableStateOf(state.value.circle.coordinates)
    var circleRadius = mutableStateOf(25.0)
    var circleVisibility = mutableStateOf(false)
    var enabled = mutableStateOf(true)
    var timePassedInSeconds = mutableStateOf(0)
    var manIsOverboard = mutableStateOf(false)
    var buttonText = "Start søk"

    // PopUp
    var manIsOverboardInfoPopup by mutableStateOf(true)
    var reiseplanleggerInfoPopup by mutableStateOf(true)

    var showDialog by mutableStateOf(false)

    val oceanViewModel =
        OceanViewModel("$oceanURL?lat=${circleCenter.value.latitude}&lon=${circleCenter.value.longitude}")

    var mapUpdateThread = MapUpdateThread(this)

    /** Resets search-area data */
    fun restartButton() {
        mapUpdateThread.isRunning = false
        circleCenter.value = state.value.circle.coordinates
        circleRadius.value = 25.0
        circleVisibility.value = false
        enabled.value = true
        timePassedInSeconds.value = 0
        manIsOverboard.value = false
        polyLinesMap.clear()
    }

    /** Starts the thread that updates the projected search-area */
    fun startButton(state: Location?, pos: LatLng) {
        circleCenter.value = locationToLatLng(state)
        circleVisibility.value = true
        enabled.value = true
        manIsOverboard.value = true
        oceanViewModel.setPath(circleCenter.value)
        oceanViewModel.getOceanForecastResponse()
        markersMapScreen.add(pos)
        mapUpdateThread = MapUpdateThread(this)
        mapUpdateThread.start()
    }

    fun updateMap(waittime: Long) {
        timePassedInSeconds.value += waittime.toInt()
        circleCenter.value = calculateNewDriftedPositionAndCircleSize(
            circleCenter.value, oceanViewModel, waittime.toDouble() / 60.0
        )
        circleRadius.value = calculateRadius(timePassedInSeconds.value / 60)
    }


    /** Updates the variable of lastKnownLocation to the units coordinate */
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

    /**This function is called when the user want to end the search */
    fun stopSearchPopupYes(text: String) {
        showDialog = false
        restartButton()
        buttonText = text
    }


    /**This function is called when the user pressed the button to start search in MannOverBord.kt*/
    fun mannOverBordButtonPress(
        connection: CheckInternet,
        internetPopupState: InternetPopupState,
        state: MapState,
        seaOrLandUrl: String
    ) {
        if (!connection.checkNetwork()) {
            internetPopupState.checkInternetPopup.value = true
        } else {
            updateLocation()
            val pos = locationToLatLng(state.lastKnownLocation)
            val seaOrLandViewModel =
                SeaOrLandViewModel("$seaOrLandUrl?latitude=${pos.latitude}&longitude=${pos.longitude}")

            viewModelScope.launch {
                if (!mapUpdateThread.isRunning) {
                    // Checks if the coordinate of the user is on land or not.
                    val seaOrLandResponse = seaOrLandViewModel.getSeaOrLandResponse()

                    // Continues if the users coordinate returns true on water
                    if (seaOrLandResponse.water) {
                        startButton(state.lastKnownLocation, pos)
                        buttonText = "Stopp søk"
                    } else {
                        manIsOverboardInfoPopup = true
                    }
                } else {
                    showDialog = true
                }
            }
        }
    }

    /** Updates the lines that show where the projected search-area has moved  */
    fun updateMarkerAndPolyLines() {
        markersMapScreen.add(circleCenter.value)
        if (markersMapScreen.size > 1) {
            val lastPosition = markersMapScreen[markersMapScreen.size - 2]
            val options = PolylineOptions().add(lastPosition, markersMapScreen.last())
                .color(android.graphics.Color.BLACK)
            polyLinesMap.add(options)
        }
    }

    /** Removes the last marker in Reiseplanlegger. Is called upon when the user removes a marker */
    fun removeLastMarker() {
        if (markerPositions.size >= 2) {
            // Remove the last marker position
            markerPositions.removeLast()
            coordinatesToFindDistanceBetween.removeLast()

            // Remove the last polyline and update the distance and time
            polyLines.removeLast()

            if (coordinatesToFindDistanceBetween.size > 1) {
                distanceInMeters.value = calculateDistance(coordinatesToFindDistanceBetween)
                lengthInMinutes.value =
                    calculateTimeInMinutes(distanceInMeters.value, speedNumber.value)
            }
        } else if (markerPositions.size == 1) {
            // If there is only one marker position left, remove it and update the displayed text
            if (!usingMyPositionTidsbruk.value) {
                markerPositions.removeLast()
                coordinatesToFindDistanceBetween.removeLast()
            }
        }
        updateDisplayedText()
    }

    /** Updates the displayed text for the user */
    fun updateDisplayedText() {
        if (speedNumber.value == 0f) {
            displayedText.value = "Du vil ikke komme fram hvis du kjører 0 knop"

        } else {
            if (markerPositions.size < 2) {
                displayedText.value =
                    "Du kan legge til en destinasjon ved å holde inne et sted på kartet. "
            } else {
                displayedText.value = formatTime(lengthInMinutes.value)
            }
        }
    }

    /** Updates whether the user wants to use his or hers position or not in Reiseplanlegger  */
    fun updateUseOfCurrentLocation(state: MapState) {
        usingMyPositionTidsbruk.value = !usingMyPositionTidsbruk.value

        if (!usingMyPositionTidsbruk.value) {
            markerPositions.removeFirst()
            coordinatesToFindDistanceBetween.removeFirst()

            if (polyLines.isNotEmpty()) {
                polyLines.removeFirst()
            }
        } else {
            markerPositions.add(0, locationToLatLng(state.lastKnownLocation))
            coordinatesToFindDistanceBetween.add(0, markerPositions[0])
            if (coordinatesToFindDistanceBetween.size >= 2) {
                val polyLine = PolylineOptions().add(markerPositions[0], markerPositions[1])
                    .color(android.graphics.Color.BLACK)
                polyLines.add(0, polyLine)
            }
        }
        distanceInMeters.value = calculateDistance(coordinatesToFindDistanceBetween)
        lengthInMinutes.value = calculateTimeInMinutes(distanceInMeters.value, speedNumber.value)

        updateDisplayedText()
    }


    /** Sets the new speed for the handle in Reiseplanlegger*/
    fun onSpeedChange(value: Float) {
        speedNumber.value = value.roundToInt().toFloat()
        lengthInMinutes.value = calculateTimeInMinutes(distanceInMeters.value, speedNumber.value)
        updateDisplayedText()
        updateLocation()
    }

    /** Adds a new marker to the map when user hold the map to select a destination-point */
    fun onLongPress(position: LatLng, state: MapState) {
        if (!lockMarkers.value) {
            // Updates the current location
            updateLocation()
            if (markerPositions.isEmpty()) {
                markerPositions += position
                coordinatesToFindDistanceBetween.add(position)
            } else {
                // Updates the first marker position to the current location
                if (usingMyPositionTidsbruk.value) {
                    markerPositions[0] = locationToLatLng(state.lastKnownLocation!!)
                    if (polyLines.size >= 1) {
                        val updatedFirstPolyLine = PolylineOptions().add(
                            markerPositions[0], markerPositions[1]
                        ).color(android.graphics.Color.RED)
                        polyLines[0] = updatedFirstPolyLine
                    }
                }
                // Adds the new marker position and coordinate for calculating distance
                markerPositions.add(position)
                coordinatesToFindDistanceBetween.add(position)

                // Adds a new polyline between the last two markers
                val lastPosition = markerPositions[markerPositions.size - 2]
                val options =
                    PolylineOptions().add(lastPosition, position).color(android.graphics.Color.RED)

                polyLines.add(options)

                if (coordinatesToFindDistanceBetween.size > 1) {
                    distanceInMeters.value = calculateDistance(coordinatesToFindDistanceBetween)
                    lengthInMinutes.value = calculateTimeInMinutes(
                        distanceInMeters.value, speedNumber.value
                    )
                }
            }
            updateDisplayedText()
        }
    }
}


/** Calculates the new position of the center in projected search-area in Mann-over-bord.
 *  Also checks if the person also has drifted more than 400 meters in  N/S/E/W direction.
 *       If that is the case, also update the oceanforecast to the new grid's measures.
 * */
fun calculateNewDriftedPositionAndCircleSize(
    personCoordinate: LatLng, ovm: OceanViewModel, time: Double
): LatLng {
    Log.i("MapScreen", "New Pos from $personCoordinate")
    val dataCoordinate = ovm.oceanForecastResponseObject.geometry.coordinates
    val dataLatLng = LatLng(dataCoordinate[1], dataCoordinate[0])

    if (hasChangedGrid(dataLatLng, personCoordinate)) {
        ovm.setPath(personCoordinate)
        ovm.getOceanForecastResponse()
    }
    //Finds the Timesery (object with oceandata) that is closest timestamp
    val forecastDetails =
        findClosestDetailsToCurrentTime(ovm.oceanForecastResponseObject.properties.timeseries)

    return calculateNewPositionFromWaveData(
        listOf(personCoordinate.latitude, personCoordinate.longitude),
        forecastDetails.sea_surface_wave_from_direction,
        forecastDetails.sea_water_speed,
        time
    )
}

/**finds the center of coordinates given in list:
 * Takes in 2 coordinates, the position of the measurement from ocean forecast and the person overboard.
 * Calculates the change in longitude and latitude. The abs function removes the sign of the number. abs(-30) = 30, abs(30) = 30.
 * Calculates the number of meters between the longitude and latitude.
 * Returns true if the person has drifted either 400m in longitude or in latitude.  */
fun hasChangedGrid(dataCoordinate: LatLng, personCoordinate: LatLng): Boolean {
    // approx distance in meters per degree of latitude, adjusted for the earths curvature
    val latDistancePerDegree = 111000
    val latdiff = abs(dataCoordinate.latitude - personCoordinate.latitude) * latDistancePerDegree

    val earthRadius = 6371e3 // Earth's radius in meters
    val latRad = Math.toRadians(dataCoordinate.latitude)
    val longdiff =
        abs(dataCoordinate.longitude - personCoordinate.longitude) * PI / 180 * cos(latRad) * earthRadius

    val answer = latdiff > 400.0 || longdiff > 400.0
    Log.i("DriftCheck:", "$dataCoordinate, $personCoordinate | data, person. New grid = $answer")
    return answer

}


/** Returns a coordinate given a coordinate, how fast the water is moving at the coordinate, which way the water is moving and how long it has been since last iteration.
 * Uses trigonometrics to calculate the new coordinate */
fun calculateNewPositionFromWaveData(
    coordinatesStart: List<Double>,
    seaMovementDirection: Double,
    seaWaterSpeedInMeters: Double,
    timeCheckingForInMinutes: Double
): LatLng {

    // Convert from degrees to radians
    val waveFromInRadians = Math.toRadians(seaMovementDirection)
    val earthRadiusInKm = 6371
    val startLatInRadians = Math.toRadians(coordinatesStart[0])
    val startLngInRadians = Math.toRadians(coordinatesStart[1])

    // m/s -> km/h
    val waterSpeedInKmPerHour = seaWaterSpeedInMeters * 3.6

    // Convert the time interval to hours
    val timeIntervalInHours = timeCheckingForInMinutes / 60.0

    // Calculate the distance traveled by the object in the given time interval
    val distanceInKm = waterSpeedInKmPerHour * timeIntervalInHours

    // Calculate the new latitude and longitude
    val newLatInRadians = asin(
        sin(startLatInRadians) * cos(distanceInKm / earthRadiusInKm) + cos(startLatInRadians) * sin(
            distanceInKm / earthRadiusInKm
        ) * cos(waveFromInRadians)
    )
    val newLngInRadians = startLngInRadians + atan2(
        sin(waveFromInRadians) * sin(distanceInKm / earthRadiusInKm) * cos(startLatInRadians),
        cos(distanceInKm / earthRadiusInKm) - sin(startLatInRadians) * sin(newLatInRadians)
    )

    // Convert back from radians to degrees
    val newLat = Math.toDegrees(newLatInRadians)
    val newLng = Math.toDegrees(newLngInRadians)

    Log.i("Calulated position", "${distanceInKm * 1000}m towards $seaMovementDirection")
    return LatLng(newLat, newLng)
}

/** Calculates the radius of the search-area */
fun calculateRadius(minutes: Int): Double {
    val newRadius: Double = minutes * 5.0
    return if (newRadius > 575.0) 575.0
    else if (newRadius < 25.0) 25.0
    else newRadius
}

/** Fetches the list of wave data closest to the current time. */
@SuppressLint("SimpleDateFormat")
fun findClosestDetailsToCurrentTime(listOfTime: List<Timesery>): Details {
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

        val secondsBetween = getSecondsBetweenTwoDates(currentTime, checkTime)
        if (secondsBetween in 0 until smallestSecondsBetween) {
            smallestIndex = i
            smallestSecondsBetween = secondsBetween
        }
        i++
    }

    return listOfTime[smallestIndex].data.instant.details
}

fun getSecondsBetweenTwoDates(date1: Date, date2: Date): Long {
    val diffInMilliseconds = abs(date1.time - date2.time)
    return TimeUnit.MILLISECONDS.toSeconds(diffInMilliseconds)
}

/** brukes for å hente posisjonen fra state. default hvis null*/
fun locationToLatLng(loc: Location?): LatLng {
    if (loc != null) {
        return LatLng(loc.latitude, loc.longitude)
    }
    Log.i("locationToLatLng", "Fant ingen location. Returnerer default LatLng(59.0, 11.0)")
    return LatLng(59.0, 11.0) //default val i oslofjorden
}












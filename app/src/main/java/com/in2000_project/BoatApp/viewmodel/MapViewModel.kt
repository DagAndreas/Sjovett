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
import com.in2000_project.BoatApp.compose.calculateNewPosition
import com.in2000_project.BoatApp.compose.calculateRadius
import com.in2000_project.BoatApp.compose.locationToLatLng
import com.in2000_project.BoatApp.maps.CircleInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

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
            val sleep_delay:Long = 3 //sekunder
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

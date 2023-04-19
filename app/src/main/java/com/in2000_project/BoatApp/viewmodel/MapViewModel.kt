package com.in2000_project.BoatApp.viewmodel

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.CurrentLocationRequest
import com.in2000_project.BoatApp.maps.*
import com.in2000_project.BoatApp.data.MapState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.in2000_project.BoatApp.R
import com.in2000_project.BoatApp.compose.calculateNewPosition
import com.in2000_project.BoatApp.compose.calculateRadius
import com.in2000_project.BoatApp.compose.oceanURL
import com.in2000_project.BoatApp.maps.CircleInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class MapViewModel @Inject constructor(): ViewModel() {


    //Koordinater settes til default: 59.0373, 10.5883

    lateinit var locationProviderClient: FusedLocationProviderClient
    fun setClient(fusedLocationProviderClient: FusedLocationProviderClient) {
        locationProviderClient = fusedLocationProviderClient
    }
    private val _state = MutableStateFlow(
        MapState(
            lastKnownLocation = null,
            circle = CircleInfo(
                coordinates = LatLng(59.0373, 10.5883),
                radius = 250.0
            )
        )
    )

    val state: StateFlow<MapState> = _state.asStateFlow()

    //var myPosition =  mutableStateOf(locationToLatLng(_state.value.lastKnownLocation!!))

    var displayedText = mutableStateOf("Du kan legge til en destinasjon ved å holde inne et sted på kartet. ")


    var distanceInMeters = mutableStateOf(0.0)
    var lengthInMinutes = mutableStateOf(0.0)
    var polyLines =  mutableStateListOf<PolylineOptions>()
    var lockMarkers =  mutableStateOf(false)
    var usingMyPositionTidsbruk = mutableStateOf(false)

    // Convert location to LatLng
    fun locationToLatLng(location: Location): LatLng {
        return LatLng(location.latitude, location.longitude)
    }

    var markerPositions =  mutableStateListOf<LatLng>()
    //.apply { myPosition.value?.let { add(it) } }
    var speedNumber =    mutableStateOf(15f)
    // distance between all of the markers
    var coordinatesToFindDistanceBetween = mutableStateListOf<LatLng>()
    val markersMapScreen = mutableListOf<LatLng>()
    val polyLinesMap = mutableListOf<PolylineOptions>()
    var circleCenter = mutableStateOf(state.value.circle.coordinates)
    var circleRadius = mutableStateOf(200.0)
    var circleVisibility = mutableStateOf(false)
    var enabled = mutableStateOf(true)
    var counter = mutableStateOf( 0 )

    var mann_er_overbord = mutableStateOf(false)
    var currentLat: Double = if (state.value.lastKnownLocation != null) state.value.lastKnownLocation!!.latitude else 56.0646
    var currentLong: Double = if (state.value.lastKnownLocation != null) state.value.lastKnownLocation!!.longitude else 10.6778
    var followCircle: Boolean = false;

    val oceanViewModel = OceanViewModel("$oceanURL?lat=${circleCenter.value.latitude}&lon=${circleCenter.value.longitude}")

    val mapUpdateThread = MapUpdateThread(this)
    class MapUpdateThread(
        val mapViewModel: MapViewModel
    ) : Thread() {
        override fun run() {
            while(true){
                sleep(5000) // x antall sek
                mapViewModel.updateMap(mapViewModel)
                Log.i("HIEIHEIEHIE", "HDASDHJKASDKASJHDJAKSD")

            }
        }
    }


    fun updateMap(mapViewModel: MapViewModel){
        val time_to_wait_in_minutes: Float = 0.025f //1.0f er 1 minutt. 0.1 = 6sek
        Log.i("MapScreen", "$time_to_wait_in_minutes minutter")

        counter.value++
        circleCenter.value = calculateNewPosition(circleCenter.value, oceanViewModel, time_to_wait_in_minutes.toDouble()*3000)
        circleRadius.value = calculateRadius(counter.value)

        markersMapScreen.add(circleCenter.value)
        if(markersMapScreen.size>1){
            val lastPosition = mapViewModel.markersMapScreen[mapViewModel.markersMapScreen.size - 2]
            val options = PolylineOptions()
                .add(lastPosition, mapViewModel.markersMapScreen.last())
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

    fun changeCircleCoordinate(newCoordinate: LatLng) {
        _state.value = state.value.copy(circle = state.value.circle.copy(coordinates = newCoordinate))
    }

    fun changeCircleRadius(newRadius: Double) {
        _state.value = state.value.copy(circle = state.value.circle.copy(radius = newRadius))
    }

    @JvmName("getState1")
    fun getState(): MapState {
        return _state.value
    }

}

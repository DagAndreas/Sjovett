package com.in2000_project.BoatApp.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.CurrentLocationRequest
import com.in2000_project.BoatApp.maps.*
import com.in2000_project.BoatApp.data.MapState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
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
    lateinit var locationProviderClient: FusedLocationProviderClient
    fun setClient(fusedLocationProviderClient: FusedLocationProviderClient) {
        locationProviderClient = fusedLocationProviderClient
    }
    private val _state = MutableStateFlow(
        MapState(
            lastKnownLocation = null,
            circle = CircleInfo(
                coordinates = LatLng(50.0, 50.0),
                radius = 250.0
            )
        )
    )

    val state: StateFlow<MapState> = _state.asStateFlow()

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

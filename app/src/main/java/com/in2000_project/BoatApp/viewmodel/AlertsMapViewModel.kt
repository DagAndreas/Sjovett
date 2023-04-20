package com.in2000_project.BoatApp.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.in2000_project.BoatApp.maps.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolygonOptions
import com.in2000_project.BoatApp.ZoneClusterItem
import com.in2000_project.BoatApp.data.MapStateCluster
import com.in2000_project.BoatApp.ZoneClusterManager
import com.in2000_project.BoatApp.data.AlertsMapUiState
import com.in2000_project.BoatApp.data.StormWarningUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AlertsMapViewModel @Inject constructor(): ViewModel() {
    val listOfClusters = mutableListOf<ZoneClusterItem>()

    private val _alertsMapUiState = MutableStateFlow(AlertsMapUiState())
    val alertsMapUiState = _alertsMapUiState.asStateFlow()

    private val _state = MutableStateFlow(
        MapStateCluster(
            lastKnownLocation = null,
            clusterItems = listOfClusters
        )
    )

    //InfoKort
    var stormvarselInfoPopUp by mutableStateOf(true)

    val state: StateFlow<MapStateCluster> = _state.asStateFlow()

    fun updateUserLocation(lat: Double, lng: Double) {
        _alertsMapUiState.update {
            // setter warningList til å være en MetAlertsResponse
            (it.copy(longitude = lng, latitude = lat))
        }
    }

    fun addCluster(
        id: String,
        title: String,
        description: String,
        polygonOptions: PolygonOptions
    ){
        listOfClusters.add(ZoneClusterItem(id, title, description, polygonOptions))
        _state.update{
            MapStateCluster(
                lastKnownLocation = null,
                clusterItems = listOfClusters
            )
        }
        //Oppdatere state?
    }

    fun resetCluster() {
        listOfClusters.clear()
        _state.update{
            MapStateCluster(
                lastKnownLocation = null,
                clusterItems = listOfClusters
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun getDeviceLocation(
        fusedLocationProviderClient: FusedLocationProviderClient
    ) {
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



    fun setupClusterManager(
        context: Context,
        map: GoogleMap,
    ): ZoneClusterManager {
        val clusterManager = ZoneClusterManager(context, map)
        clusterManager.addItems(state.value.clusterItems)
        return clusterManager
    }

    fun calculateZoneLatLngBounds(): LatLngBounds {
        // Get all the points from all the polygons and calculate the camera view that will show them all.
        val latLngs = state.value.clusterItems.map { it.polygonOptions }
            .map { it.points.map { LatLng(it.latitude, it.longitude) } }.flatten()
        return latLngs.calculateCameraViewPoints().getCenterOfPolygon()
    }


}

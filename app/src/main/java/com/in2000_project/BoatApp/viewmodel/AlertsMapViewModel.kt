package com.in2000_project.BoatApp.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolygonOptions
import com.in2000_project.BoatApp.data.AlertsMapUiState
import com.in2000_project.BoatApp.data.MapStateCluster
import com.in2000_project.BoatApp.maps.ZoneClusterItem
import com.in2000_project.BoatApp.maps.ZoneClusterManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AlertsMapViewModel @Inject constructor(): ViewModel() {
    private val listOfClusters = mutableListOf<ZoneClusterItem>()

    private val _alertsMapUiState = MutableStateFlow(AlertsMapUiState())
    val alertsMapUiState = _alertsMapUiState.asStateFlow()


    private val _state = MutableStateFlow(
        MapStateCluster(
            lastKnownLocation = null,
            clusterItems = listOfClusters
        )
    )

    //InfoCards
    var stormvarselInfoPopUp by mutableStateOf(true)

    val state: StateFlow<MapStateCluster> = _state.asStateFlow()

    fun updateUserLocation(lat: Double, lng: Double) {
        _alertsMapUiState.update {
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
    fun setupClusterManager(
        context: Context,
        map: GoogleMap,
    ): ZoneClusterManager {
        val clusterManager = ZoneClusterManager(context, map)
        clusterManager.addItems(state.value.clusterItems)
        return clusterManager
    }

}

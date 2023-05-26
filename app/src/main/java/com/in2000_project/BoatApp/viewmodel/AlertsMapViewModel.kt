package com.in2000_project.BoatApp.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolygonOptions
import com.in2000_project.BoatApp.data.MapStateCluster
import com.in2000_project.BoatApp.maps.ZoneClusterItem
import com.in2000_project.BoatApp.maps.ZoneClusterManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.system.exitProcess

@HiltViewModel
class AlertsMapViewModel @Inject constructor() : ViewModel() {
    private val listOfClusters = mutableListOf<ZoneClusterItem>()

    lateinit var locationProviderClient: FusedLocationProviderClient
    fun setClient(fusedLocationProviderClient: FusedLocationProviderClient) {
        locationProviderClient = fusedLocationProviderClient
    }

    private val _state = MutableStateFlow(
        MapStateCluster(
            lastKnownLocation = null, clusterItems = listOfClusters
        )
    )
    val state: StateFlow<MapStateCluster> = _state.asStateFlow()

    //InfoCards
    var stormvarselInfoPopUp by mutableStateOf(true)


    /** Adds the clusters to _state */
    fun addCluster(
        id: String, title: String, description: String, polygonOptions: PolygonOptions
    ) {
        listOfClusters.add(ZoneClusterItem(id, title, description, polygonOptions))
        _state.update {
            it.copy(
                clusterItems = listOfClusters,
            )
        }
    }

    /** Clears _state of clusters*/
    fun resetCluster() {
        listOfClusters.clear()
        _state.update {
            it.copy(
                clusterItems = listOfClusters,
            )
        }
    }

    /** Sets up a cluster manager to handle storms */
    fun setupClusterManager(
        context: Context,
        map: GoogleMap,
    ): ZoneClusterManager {
        val clusterManager = ZoneClusterManager(context, map)
        clusterManager.addItems(state.value.clusterItems)
        return clusterManager
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
            exitProcess(-1)
        }
    }

}

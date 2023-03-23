package com.codingwithmitch.composegooglemaps

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Insets.add
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.codingwithmitch.composegooglemaps.clusters.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.ktx.model.circleOptions
import com.google.maps.android.ktx.model.markerOptions
import com.google.maps.android.ktx.model.polygonOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(): ViewModel() {

    val state: MutableState<MapState> = mutableStateOf(
        MapState(
            lastKnownLocation = null,
            clusterItems = listOf(
                ZoneClusterItem(
                    id = "zone-3",
                    title = "Zone 3",
                    snippet = "This is Zone 3.",
                    circleOptions = circleOptions {
                        center(LatLng(59.911491, 10.757933))
                        radius(250.0)
                        fillColor(CIRCLE_FILL_COLOR)
                        strokeWidth(2F)
                        markerOptions { visible(false) } //funker ikke
                    } // circleOptions
                )
                /*
                ZoneClusterItem(

                    id = "zone-1",
                    title = "Zone 1",
                    snippet = "This is Zone 1.",
                    polygonOptions = polygonOptions {
                        /*
                        Her vil vi sette en latitude, og longitude og regne ut
                        hjørnene basert på disse. Markøren blir plassert midt i
                        polygon
                         */
                        add(LatLng(49.105, -122.524))
                        add(LatLng(49.101, -122.529))
                        add(LatLng(49.092, -122.501))
                        add(LatLng(49.1, -122.506))
                        fillColor(POLYGON_FILL_COLOR)
                    }
                ),
                ZoneClusterItem(
                    id = "zone-2",
                    title = "Zone 2",
                    snippet = "This is Zone 2.",
                    polygonOptions = polygonOptions {
                        val lat = 50.0
                        val long = 50.0
                        add(LatLng(lat, long))
                        add(LatLng(lat, long))
                        add(LatLng(lat, long))
                        fillColor(POLYGON_FILL_COLOR)
                    }
                ),*/
            ) // list of
        )
    )

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
                    state.value = state.value.copy(
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
/*
    fun calculateZoneLatLngBounds(): LatLngBounds {
        // Get all the points from all the polygons and calculate the camera view that will show them all.
        val latLngs = state.value.clusterItems.map { it.polygonOptions }
                .map { it.points.map { LatLng(it.latitude, it.longitude) } }.flatten()
       return latLngs.calculateCameraViewPoints().getCenterOfPolygon()
    }


*/
    companion object {
        private val CIRCLE_FILL_COLOR = Color.parseColor("#ABF44336")
    }

}
package com.in2000_project.BoatApp.data

import android.location.Location
import com.in2000_project.BoatApp.maps.ZoneClusterItem

/** Holds the state of the clusters of a map */
data class MapStateCluster(
    val lastKnownLocation: Location?,
    val clusterItems: List<ZoneClusterItem>
)
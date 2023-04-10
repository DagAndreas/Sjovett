package com.in2000_project.BoatApp.data

import android.location.Location
import com.in2000_project.BoatApp.ZoneClusterItem

data class MapStateCluster (
    val lastKnownLocation: Location?,
    val clusterItems: List<ZoneClusterItem>
)
package com.in2000_project.BoatApp.data

import android.location.Location
import com.in2000_project.BoatApp.maps.CircleInfo

data class MapState(
    val lastKnownLocation: Location?,
    val circle: CircleInfo
)

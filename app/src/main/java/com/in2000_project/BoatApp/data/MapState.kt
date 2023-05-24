package com.in2000_project.BoatApp.data

import android.location.Location

data class MapState(
    val lastKnownLocation: Location?,
    val circle: CircleState
)

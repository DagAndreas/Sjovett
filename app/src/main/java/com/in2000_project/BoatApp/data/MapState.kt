package com.in2000_project.BoatApp.data

import android.location.Location

/** Holds the state of a map  */
data class MapState(
    val lastKnownLocation: Location?, val circle: CircleState
)

package com.in2000_project.BoatApp.data

import com.google.android.gms.maps.model.LatLng

/** Holds the state of the circle used in Mann-over-bord*/
data class CircleState(
    val coordinates: LatLng, val radius: Double
)

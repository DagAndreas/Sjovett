package com.in2000_project.BoatApp.maps

import com.google.android.gms.maps.model.LatLng

// TODO: Flytt til maps-mappe i Model
data class CircleInfo(
    val coordinates: LatLng,
    val radius: Double
)

package com.codingwithmitch.composegooglemaps.clusters

import com.google.android.gms.maps.model.LatLng

data class CircleInfo(
    val coordinates: LatLng,
    val radius: Double
)

package com.codingwithmitch.composegooglemaps.data

import android.location.Location
import com.codingwithmitch.composegooglemaps.clusters.CircleInfo
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.LatLng

data class MapState(
    val lastKnownLocation: Location?,
    val circle: CircleInfo
)

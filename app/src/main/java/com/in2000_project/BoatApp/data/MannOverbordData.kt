package com.in2000_project.BoatApp.data

import androidx.compose.runtime.*
import com.google.android.gms.maps.model.LatLng

data class MannOverbordData(
    var circleCenter: MutableState<LatLng> = mutableStateOf(LatLng(0.0, 0.0)),
    var circleRadius: MutableState<Double> = mutableStateOf(200.0),
    var circleVisible: MutableState<Boolean> = mutableStateOf(false),
    var enabled: MutableState<Boolean> = mutableStateOf(true),
    var counter: MutableState<Int> = mutableStateOf(0),
    var manOverboard: MutableState<Boolean> = mutableStateOf(false),
    var currentLat: Double = 56.0646,
    var currentLong: Double = 10.6778
)
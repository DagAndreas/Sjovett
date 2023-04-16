package com.in2000_project.BoatApp.model.seaOrLand

data class SeaOrLandResponse(
    val latitude: Double,
    val longitude: Double,
    val water: Boolean
)
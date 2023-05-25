package com.in2000_project.BoatApp.model.seaOrLand
/** We use this data class in isItWater to check if a given coordinate is on land or not */

data class SeaOrLandResponse(
    val latitude: Double,
    val longitude: Double,
    val water: Boolean
)
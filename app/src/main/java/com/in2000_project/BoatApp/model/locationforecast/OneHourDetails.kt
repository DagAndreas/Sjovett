package com.in2000_project.BoatApp.model.locationforecast

data class OneHourDetails(
    val precipitation_amount: Double,
    val precipitation_amount_max: Double,
    val precipitation_amount_min: Double,
    val probability_of_precipitation: Double,
    val probability_of_thunder: Double

)

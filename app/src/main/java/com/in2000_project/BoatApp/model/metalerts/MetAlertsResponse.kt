package com.example.gruppe_16.model.metalerts

/** We use this data class in metAlerts to display the proper data */
data class MetAlertsResponse(
    val features: List<Feature>,
    val lang: String,
    val lastChange: String,
    val type: String
)
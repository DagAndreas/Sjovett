package com.example.gruppe_16.model.metalerts

data class Properties(
    val area: String,
    val awarenessResponse: String,
    val awarenessSeriousness: String,
    val awareness_level: String,
    val awareness_type: String,
    val certainty: String,
    val consequences: String,
    val county: List<String>,
    val description: String,
    val event: String,
    val eventAwarenessName: String,
    val eventEndingTime: String,
    val geographicDomain: String,
    val id: String,
    val instruction: String,
    val resources: List<Resource>,
    val severity: String,
    val title: String,
    val triggerLevel: String,
    val type: String
)
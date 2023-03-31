package com.example.gruppe_16.model.metalerts

data class Feature(
    val geometry: Geometry,
    val properties: Properties,
    val type: String,
    val `when`: When
)

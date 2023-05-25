package com.in2000_project.BoatApp.data

import com.example.gruppe_16.model.metalerts.Feature

/** Holds the state of a list of warnings. Used in Stormvarsel */
data class StormvarselUiState(
    val warningList: List<Feature> = emptyList()
)
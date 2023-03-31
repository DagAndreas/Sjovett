package com.in2000_project.BoatApp.data

import com.example.gruppe_16.model.metalerts.Feature


data class StormWarningUiState(
    val warningList: List<Feature> = emptyList()
)
package com.codingwithmitch.composegooglemaps.data

import com.example.gruppe_16.model.metalerts.Feature


data class StormWarningUiState(
    val warningList: List<Feature> = emptyList()
)
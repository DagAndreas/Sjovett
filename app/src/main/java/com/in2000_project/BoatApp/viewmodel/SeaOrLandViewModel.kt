package com.in2000_project.BoatApp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.in2000_project.BoatApp.data.ApiDataSource
import com.in2000_project.BoatApp.model.seaOrLand.SeaOrLandResponse

class SeaOrLandViewModel(urlPath: String) : ViewModel() {

    private val _dataSource = ApiDataSource()
    var path: String = urlPath
    private var amountOfTimesFetched = 0
    private var seaOrLandResponse = SeaOrLandResponse(0.0, 0.0, true)

    suspend fun getSeaOrLandResponse(): SeaOrLandResponse {
        try {
            seaOrLandResponse = _dataSource.fetchSeaOrLandResponse(path)
            amountOfTimesFetched++
            Log.i("SeaOrLandViewModel", "Fetched coordinate data $amountOfTimesFetched times")
        } catch (e: Exception) {
            Log.e("getSeaOrLandResponse", e.toString())
        }
        return seaOrLandResponse
    }
}


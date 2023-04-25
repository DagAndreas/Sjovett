package com.in2000_project.BoatApp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.in2000_project.BoatApp.data.ApiDataSource
import com.in2000_project.BoatApp.data.SeaOrLandDataSource
import com.in2000_project.BoatApp.model.seaOrLand.SeaOrLandResponse
import kotlinx.coroutines.launch

class SeaOrLandViewModel(urlPath: String) : ViewModel() {

    private val _dataSource = ApiDataSource()
    var path: String = urlPath
    //private val _seaOrLandResponse = MutableLiveData<SeaOrLandResponse>()
    // var seaOrLandResponse: SeaOrLandResponse = getSeaOrLandResponse()
    private var antallGangerHentet = 0

    suspend fun getSeaOrLandResponse(): SeaOrLandResponse {
        var seaOrLandResponse = SeaOrLandResponse(0.0, 0.0, true)
        try {
            seaOrLandResponse = _dataSource.fetchSeaOrLandResponse(path)
            antallGangerHentet++
            Log.i("SeaOrLandViewModel", "hentet koordinatdata $antallGangerHentet ganger")
        } catch (e: Exception) {
            // handle exception
        }
        return seaOrLandResponse
    }
}


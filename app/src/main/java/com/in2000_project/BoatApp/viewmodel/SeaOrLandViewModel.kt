package com.in2000_project.BoatApp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.in2000_project.BoatApp.data.SeaOrLandDataSource
import com.in2000_project.BoatApp.model.seaOrLand.SeaOrLandResponse
import kotlinx.coroutines.launch

class SeaOrLandViewModel(urlPath: String) : ViewModel() {

    private val _dataSource = SeaOrLandDataSource()
    private var path = urlPath

    private val _seaOrLandResponse = MutableLiveData<SeaOrLandResponse>()
    val seaOrLandResponse: LiveData<SeaOrLandResponse> = _seaOrLandResponse

    private var antallGangerHentet = 0

    init {
        getSeaOrLandResponse()
    }

    private fun getSeaOrLandResponse() {
        viewModelScope.launch {
            try {
                val response = _dataSource.fetchSeaOrLand(path)
                _seaOrLandResponse.postValue(response)
                antallGangerHentet++
                Log.i("hentet vanndata", "$antallGangerHentet ganger")
                println("Hei")
            } catch (e: Exception) {
                Log.e("API_request xxx", e.message.toString())
            }
        }
    }

}


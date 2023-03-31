package com.codingwithmitch.composegooglemaps

import com.codingwithmitch.composegooglemaps.data.ApiDataSource
import com.codingwithmitch.composegooglemaps.data.StormWarningUiState
import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MetAlertsViewModel: ViewModel() {
    val _dataSource = ApiDataSource()
    private val _stormWarningUiState = MutableStateFlow(StormWarningUiState())
    val stormWarningUiState = _stormWarningUiState.asStateFlow()

    init {
        fetchMetAlerts()
    }

    fun fetchMetAlerts() { // Henter data fra APIet
        Log.d("Fetch", "MetAlerts")
        CoroutineScope(Dispatchers.IO).launch {
            val url = "https://api.met.no/weatherapi/metalerts/1.1/.json"
            _stormWarningUiState.update {
                // setter warningList til å være en MetAlertsResponse
                (it.copy(warningList = _dataSource.fetchMetAlertsData(url).features))
            }
        }
    }
/*

curl --header "X-Gravitee-Api-Key: dc1732ae-a8a0-4dd5-8052-26094bfbca11" \
     https://gw-uio.intark.uh-it.no/in2000

    //val metAlertDataSource =
    var metAlertsResponse: MetAlertsResponse;

    fun getMetAlertsDataSource(): MetAlertsResponse{
        var metResponse: MetAlertsResponse = MetAlertsResponse(emptyList(), "", "", "")

        viewModelScope.launch {
            metResponse = metAlertDataSource.fetchMetData()
        }
        return metResponse
    }

    init {
        metAlertsResponse = getMetAlertsDataSource()
    }

 */
}


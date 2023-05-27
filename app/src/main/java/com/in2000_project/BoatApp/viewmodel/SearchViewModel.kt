package com.in2000_project.BoatApp.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.in2000_project.BoatApp.R
import com.in2000_project.BoatApp.data.ApiDataSource
import com.in2000_project.BoatApp.data.GeoCodeUiState
import com.in2000_project.BoatApp.launch.CheckInternet
import com.in2000_project.BoatApp.launch.InternetPopupState
import com.in2000_project.BoatApp.model.geoCode.CityName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class SearchViewModel(context: Context) : ViewModel() {
    private val _dataSource = ApiDataSource()
    private val _geoCodeUiState = MutableStateFlow(GeoCodeUiState())
    val geoCodeUiState = _geoCodeUiState.asStateFlow()

    private val _locationSearch = MutableStateFlow("")
    val locationSearch = _locationSearch.asStateFlow()

    private val _searchInProgress = MutableStateFlow(false)
    val searchInProgress = _searchInProgress.asStateFlow()

    private val array: Array<String> = context.resources.getStringArray(R.array.city_list)
    private val _cities = MutableStateFlow(getAllCities(array.toList()))

    val cities = locationSearch.onEach { _searchInProgress.update { true } }
        .combine(_cities) { text, cities ->
            if (text.isBlank()) {
                // Shows the entire list if the user does not type anything
                cities
            } else {
                delay(500L)
                cities.filter {
                    it.matchesSearch(text)
                }
            }
        }.onEach { _searchInProgress.update { false } }.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), _cities.value
        )


    fun onSearchChange(text: String) {
        _locationSearch.value = text.replace("\n", "")
    }


    suspend fun fetchCityData(
        cityName: String, connection: CheckInternet, internetPopupState: InternetPopupState
    ) {
        if (!connection.checkNetwork()) { // Stops the use of internet actions, if internet is not connected
            Log.e("Internet connection", "Not connected!")
            internetPopupState.checkInternetPopup.value = true
        } else {
            internetPopupState.checkInternetPopup.value = false
            _locationSearch.update {
                cityName
            }
            CoroutineScope(Dispatchers.IO).launch {
                val url = "https://api.api-ninjas.com/v1/geocoding?city=$cityName&country=Norway"
                _geoCodeUiState.update {
                    it.copy(cityList = _dataSource.fetchGeoCodeData(url))
                }
            }
        }
    }

    fun resetCityData() {
        _geoCodeUiState.update {
            it.copy(cityList = emptyList())
        }
    }
}


fun getAllCities(cities: List<String>): MutableList<CityName> {
    val listOfCities = emptyList<CityName>().toMutableList()
    for (city in cities) {
        listOfCities.add(CityName(city, "Norway"))
    }
    return listOfCities
}

package com.in2000_project.BoatApp.viewmodel

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.in2000_project.BoatApp.R
import com.in2000_project.BoatApp.data.ApiDataSource
import com.in2000_project.BoatApp.data.GeoCodeUiState
import com.in2000_project.BoatApp.model.geoCode.CityName
import com.in2000_project.BoatApp.CheckInternet
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.hamcrest.Condition.Step


class SearchViewModel(context: Context): ViewModel() {
    private val _dataSource = ApiDataSource()
    private val _geoCodeUiState = MutableStateFlow(GeoCodeUiState())
    val geoCodeUiState = _geoCodeUiState.asStateFlow()

    private val _locationSearch = MutableStateFlow("")
    val locationSearch = _locationSearch.asStateFlow()

    private val _searchInProgress = MutableStateFlow(false)
    val searchInProgress = _searchInProgress.asStateFlow()

    private val array: Array<String> = context.resources.getStringArray(R.array.city_list)
    private val _cities = MutableStateFlow(getAllCities(array.toList()))

    val cities = locationSearch
        .onEach { _searchInProgress.update{true}}
        .combine(_cities){ text, cities ->
            if(text.isBlank()){
                // Shows the entire list if the user does not type anything
                cities
            }else{
                delay(500L)
                cities.filter{
                    it.matchesSearch(text)
                }
            }
        }
        .onEach{_searchInProgress.update{false}}
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _cities.value
        )


    fun onSearchChange(text: String){
        _locationSearch.value = text.replace("\n", "")
    }


    @RequiresApi(Build.VERSION_CODES.M)
    suspend fun fetchCityData(cityName: String, connection: CheckInternet) {
        if (!connection.checkNetwork()) { // Stops the use of internet actions, if internet is not connected
            Log.e("Internet connection", "Not connected!")
        } else {
            _locationSearch.update{
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


fun getAllCities(cities: List<String>): MutableList<CityName>{

    val listOfCities = emptyList<CityName>().toMutableList()
    for(city in cities){
        listOfCities.add(CityName(city, "Norway"))
    }
    return listOfCities
}

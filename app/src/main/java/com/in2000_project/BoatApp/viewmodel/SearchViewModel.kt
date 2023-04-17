package com.in2000_project.BoatApp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.in2000_project.BoatApp.model.geoCode.CityName
import kotlinx.coroutines.flow.*

class SearchViewModel: ViewModel() {
    private val _locationSearch = MutableStateFlow("")
    val locationSearch = _locationSearch.asStateFlow()

    private val _searchInProgress = MutableStateFlow(false)
    val searchInProgress = _searchInProgress.asStateFlow()

    private val _cities = MutableStateFlow(listOf<CityName>())
    val cities = locationSearch
        .combine(_cities){ text, cities ->
            if(text.isBlank()){
                cities //viser alle steder om man ikke har begynt søk
            }else{
                cities.filter{
                    it.matchesSearch(text)
                }
            }
        }
        .stateIn( //for å få stateFlow
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _cities.value
        )


    fun onSearchChange(text: String){
        _locationSearch.value = text
    }
}



val norwegianCities = listOf(
    "Oslo",
    "Trondheim",
    "Bergen"
)

fun getAllCities(cities: List<String>): MutableList<CityName>{
    val listOfCities = emptyList<CityName>().toMutableList()
    for(city in cities){
        listOfCities.add(CityName(city, "Norway"))
    }
    return listOfCities
}
package com.in2000_project.BoatApp.data

import android.util.Log
import com.example.gruppe_16.model.locationforecast.LocationForecastResponse
import com.example.gruppe_16.model.metalerts.MetAlertsResponse
import com.example.gruppe_16.model.oceanforecast.OceanForecastResponse
import com.in2000_project.BoatApp.R

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*

import io.ktor.client.request.*
import io.ktor.serialization.gson.*


class ApiDataSource () {
    private val client = HttpClient{
        install(ContentNegotiation) {
            gson()
        }
    }

    suspend fun fetchLocationForecastData(path: String): LocationForecastResponse {
        Log.d("API_request", "attempting fetchLocationForecastData.launch")
        val response = client.get(path){
            headers{
                append(name = "X-Gravitee-Api-Key", value = "dc1732ae-a8a0-4dd5-8052-26094bfbca11")
            }
        }.body<LocationForecastResponse>()
        Log.d("API_request", "fetchLocationForecastData.launch success, response: ")
        Log.i("API_request", response.toString())
        return response
    }


    suspend fun fetchMetAlertsData(path: String): MetAlertsResponse {
        Log.d("API_request", "attempting fetchMetAlertsData.launch")
        val response = client.get(path){
            headers{
                append(name = "X-Gravitee-Api-Key", value = "dc1732ae-a8a0-4dd5-8052-26094bfbca11")
            }
        }.body<MetAlertsResponse>()
        Log.d("API_request", "fetchMetAlertsData.launch success, response: ")
        Log.i("API_request", response.toString())
        return response
    }

    suspend fun fetchOceanForecastData(path: String): OceanForecastResponse {
        Log.d("API_request", "attempting fetchOceanForecastData.launch")
        val response = client.get(path).body<OceanForecastResponse>()

        val itr = response.properties.timeseries.listIterator()
        while (itr.hasNext()){
            Log.i("oceanforecast", itr.next().toString())
        }

        Log.d("API_request", "fetchOceanForecastData.launch success, response: ")
        Log.i("API_request", response.toString())
        return response
    }
}
package com.in2000_project.BoatApp.data

import android.util.Log
import com.example.gruppe_16.model.locationforecast.LocationForecastResponse
import com.example.gruppe_16.model.metalerts.MetAlertsResponse
import com.example.gruppe_16.model.oceanforecast.OceanForecastResponse
import com.in2000_project.BoatApp.model.oceanforecast.OceanForecastResponse

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
        val response = client.get(path).body<LocationForecastResponse>()
        Log.i("FetchedLocationForecast", response.toString())
        return response
    }


    suspend fun fetchMetAlertsData(path: String): MetAlertsResponse {
        val response = client.get(path).body<MetAlertsResponse>()
        Log.i("Fetched MetAlerts api", response.toString())
        return response
    }

    suspend fun fetchOceanForecastData(path: String): OceanForecastResponse {
        val response = client.get(path).body<OceanForecastResponse>()

        val itr = response.properties.timeseries.listIterator()
        while (itr.hasNext()){
            Log.i("oceanforecast", itr.next().toString())
        }

        Log.i("Fetched OceanForecast", response.toString())
        return response
    }
}
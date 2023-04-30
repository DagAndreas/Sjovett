package com.in2000_project.BoatApp.data

import android.util.Log
import com.example.gruppe_16.model.locationforecast.LocationForecastResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.gson.*
// TODO: Tror denne kan slettes -> ApiDataSource gjør dette
class LocationForecastDataSource(val path: String) {
    private val client = HttpClient {
        install(ContentNegotiation) {
            gson()
        }
    }

    suspend fun fetchLocationForecast(): LocationForecastResponse {
        val ans = client.get(path).body<LocationForecastResponse>()
        Log.i("Fetched LocationApi ", ans.toString())
        return ans
    }
}
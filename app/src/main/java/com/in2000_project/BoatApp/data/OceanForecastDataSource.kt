package com.in2000_project.BoatApp.data

import android.util.Log
import com.example.gruppe_16.model.oceanforecast.OceanForecastResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.gson.*

class OceanForecastDataSource (val path: String){
    private val client = HttpClient{
        install(ContentNegotiation) {
            gson()
        }
    }
    suspend fun fetchOceanForecast(): OceanForecastResponse {
        val ans = client.get(path).body<OceanForecastResponse>()
        Log.i("Fetched Ocean Forecast ", ans.toString())
        return ans
    }
}

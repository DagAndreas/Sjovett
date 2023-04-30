package com.in2000_project.BoatApp.data

import android.util.Log
import com.in2000_project.BoatApp.model.oceanforecast.OceanForecastResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.gson.*
// TODO: Tror denne kan slettes -> ApiDataSource gjør dette
class OceanForecastDataSource (val path: String){
    private val client = HttpClient{
        install(ContentNegotiation) {
            gson()
        }
    }
    suspend fun fetchOceanForecast(): OceanForecastResponse {
        val ans = client.get(path).body<OceanForecastResponse>()
        Log.i("OceanForecastDataSrc ", ans.toString())
        return ans
    }
}

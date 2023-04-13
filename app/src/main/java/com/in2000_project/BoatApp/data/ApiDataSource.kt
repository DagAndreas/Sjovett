package com.in2000_project.BoatApp.data

import android.util.Log
import com.example.gruppe_16.model.locationforecast.LocationForecastResponse
import com.example.gruppe_16.model.metalerts.MetAlertsResponse
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
        Log.i("Henter lokasjonsdata", "fra $path")
        val response = client.get {
            url(path)
            headers {
                append(
                    name = "X-Api-Key",
                    value = "Ef8bkbpLK+TeaAk43qgYqw==mZBU9A3ckObEAYY7"
                )
            }
        }.body<LocationForecastResponse>()
        Log.i("FetchedLocationForecast", "$response")
        return response
    }


    suspend fun fetchMetAlertsData(path: String): MetAlertsResponse {
        Log.i("Henter alertdata", "fra $path")
        val response = client.get {
            url(path)
            headers {
                append(
                    name = "X-Api-Key",
                    value = "Ef8bkbpLK+TeaAk43qgYqw==mZBU9A3ckObEAYY7"
                )
            }
        }.body<MetAlertsResponse>()
        Log.i("Fetched MetAlerts api", "$response")
        return response
    }

    suspend fun fetchOceanForecastData(path: String): OceanForecastResponse {
        Log.i("Henter vanndata", "fra $path")
        val response = client.get {
            url(path)
            headers {
                append(
                    name = "X-Api-Key",
                    value = "Ef8bkbpLK+TeaAk43qgYqw==mZBU9A3ckObEAYY7"
                )
            }
        }.body<OceanForecastResponse>()
        Log.i("Fetched OceanForecast", "$response")
        return response
    }
}
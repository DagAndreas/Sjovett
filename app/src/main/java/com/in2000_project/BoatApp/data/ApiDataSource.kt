package com.in2000_project.BoatApp.data

import android.util.Log
import com.example.gruppe_16.model.locationforecast.LocationForecastResponse
import com.example.gruppe_16.model.metalerts.MetAlertsResponse
import com.in2000_project.BoatApp.BuildConfig
import com.in2000_project.BoatApp.model.geoCode.City
import com.in2000_project.BoatApp.model.oceanforecast.*
import com.in2000_project.BoatApp.model.seaOrLand.SeaOrLandResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.gson.*
import kotlin.system.exitProcess

class ApiDataSource {
    private val client = HttpClient{
        install(ContentNegotiation) {
            gson()
        }
    }

    suspend fun fetchLocationForecastData(path: String): LocationForecastResponse {
        Log.i("Fetching LocationData", "fra $path")
        val response = try {
            client.get {
                url(path)
                headers {
                    append(
                        name = "X-Gravitee-Api-Key",
                        value = BuildConfig.MET_KEY
                    )
                }
            }.body<LocationForecastResponse>()
        } catch (e: Exception) {
            // General exception
            Log.e("API_request xxx", path)
            Log.e("API_request xxx", e.message.toString())
            exitProcess(0)
        }
        Log.d("API_request", "fetchLocationForecastData.launch success, response: $response")
        return response
    }


    suspend fun fetchMetAlertsData(path: String): MetAlertsResponse {
        Log.d("API_request", "attempting fetchMetAlertsData.launch")
        val response = try {
            client.get {
                url(path)
                headers {
                    append(
                        name = "X-Gravitee-Api-Key",
                        value = BuildConfig.MET_KEY
                    )
                }
            }.body<MetAlertsResponse>()
        } catch (e: Exception) {
            // General exception
            Log.e("API_request xxx", path)
            Log.e("API_request xxx", e.message.toString())
            exitProcess(0) // Avslutter appen?
        }
        Log.d("API_request", "fetchMetAlertsData.launch success, response: $response")
        return response
    }

    suspend fun fetchOceanForecastData(path: String): OceanForecastResponse {
        Log.d("API_request", "attempting fetchOceanForecastData.launch")
        val response = try {
            client.get {
                url(path)//path)
                headers {
                    append(
                        name = "X-Gravitee-Api-Key",
                        value = BuildConfig.MET_KEY
                    )
                }
            }.body<OceanForecastResponse>()
        } catch (e: Exception) {
            // General exception
            Log.e("API_request xxx", path)
            Log.e("API_request xxx", e.message.toString())
            return getEmptyOceanForecastResponse()
        }

        Log.d("API_request", "fetchOceanForecastData.launch success, response: $response")
        return response
    }
    suspend fun fetchGeoCodeData(path: String): List<City> {
        Log.i("API_request", "Fetching geodata from $path")
        val response = try {
            client.get {
                url(path)
                headers {
                    append(
                        name = "X-Api-Key",
                        value = BuildConfig.GEO_KEY
                    )
                }
            }.body<List<City>>()
        } catch (e: Exception) {
            // General exception
            Log.e("API_request xxx", path)
            Log.e("API_request xxx", e.message.toString())
            exitProcess(0)
        }

        Log.d("API_request", "fetchGeoCodeData success, response: $response")
        return response
    }
    suspend fun fetchSeaOrLandResponse(path: String): SeaOrLandResponse {
        Log.i("Fetching SeaOrLand data", "fra $path")
        val response = try {
            client.get(path).body<SeaOrLandResponse>()
        }
        catch (e: Exception) {
            // General exception
            Log.e("API_request xxx", path)
            Log.e("API_request xxx", e.message.toString())
            exitProcess(0)
        }
        Log.i("API_request", "ApiData_Source_SeaOrLand success, response: $response")
        return response
    }
}
package com.in2000_project.BoatApp.data

import android.util.Log
import com.example.gruppe_16.model.locationforecast.LocationForecastResponse
import com.example.gruppe_16.model.metalerts.MetAlertsResponse
import com.in2000_project.BoatApp.model.geoCode.City
import com.in2000_project.BoatApp.model.oceanforecast.OceanForecastResponse
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
        Log.i("Henter lokasjonsdata", "fra $path")
        val response = try {
            client.get() {
                url(path)
                headers {
                    append(
                        name = "X-Gravitee-Api-Key",//R.string.Proxy_name.toString(),
                        value = "dc1732ae-a8a0-4dd5-8052-26094bfbca11"//R.string.Proxy_key.toString()
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
                        value = "dc1732ae-a8a0-4dd5-8052-26094bfbca11"
                    )
                }
            }.body<MetAlertsResponse>()
        } catch (e: Exception) { // Denne kan kanskje fjernes?
            // General exception
            Log.e("API_request xxx", path)
            Log.e("API_request xxx", e.message.toString())
            exitProcess(0) // Avslutter appen?
        }
        Log.d("API_request", "fetchMetAlertsData.launch success, response: $response")
        return response
    }

    suspend fun fetchOceanForecastData(path: String): OceanForecastResponse {
        //Log.d("API_request", "attempting fetchOceanForecastData.launch")
        val response = try {
            client.get() {
                url(path)//path)
                headers {
                    append(
                        name = "X-Gravitee-Api-Key",//R.string.Proxy_name.toString(),
                        value = "dc1732ae-a8a0-4dd5-8052-26094bfbca11"//R.string.Proxy_key.toString()
                    )
                }
            }.body<OceanForecastResponse>()
        } catch (e: Exception) {
            // General exception
            Log.e("API_request xxx", path)
            Log.e("API_request xxx", e.message.toString())
            exitProcess(0)
        }

        Log.d("API_request", "fetchOceanForecastData.launch success, response: ")
        Log.i("ApiData_Source_Ocean", "response: $response")
        return response
    }
    suspend fun fetchGeoCodeData(path: String): List<City> {
        Log.i("Henter geodata", "fra $path")
        val response = client.get {
            url(path)
            headers {
                append(
                    name = "X-Api-Key",
                    value = "Ef8bkbpLK+TeaAk43qgYqw==mZBU9A3ckObEAYY7"
                )
            }
        }.body<List<City>>()
        Log.d("Henter", response.toString())

        Log.d("API_request", "fetchGeoCodeData.launch success, response: $response")
        return response
    }
    suspend fun fetchSeaOrLandResponse(path: String): SeaOrLandResponse {
        Log.i("Henter SeaOrLand data", "fra $path")
        val response = try {
            client.get(path).body<SeaOrLandResponse>()
        }
        catch (e: Exception) {
            // General exception
            Log.e("API_request xxx", path)
            Log.e("API_request xxx", e.message.toString())
            exitProcess(0)
        }
        Log.d("API_request", "fetchSeaOrLandResponse.launch success, response: ")
        Log.i("ApiData_Source_SeaOrLan", "response: $response")
        return response
    }


}
package com.in2000_project.BoatApp.data

import android.util.Log
import com.example.gruppe_16.model.locationforecast.LocationForecastResponse
import com.example.gruppe_16.model.metalerts.MetAlertsResponse
import com.example.gruppe_16.model.oceanforecast.OceanForecastResponse
import com.in2000_project.BoatApp.R

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import java.lang.System.exit
import kotlin.system.exitProcess


class ApiDataSource () {
    private val client = HttpClient{
        install(ContentNegotiation) {
            gson()
        }
    }

    suspend fun fetchLocationForecastData(itempath: String): LocationForecastResponse {
        Log.d("API_request", "attempting fetchLocationForecastData.launch")
        val response = try {
            client.get(itempath) {
                url(itempath)
                headers {
                    append(
                        name = "X-Gravitee-Api-Key",
                        value = "dc1732ae-a8a0-4dd5-8052-26094bfbca11"
                    )
                }
            }.body<LocationForecastResponse>()
            /*
            headers {
                        append(
                            name = "X-Gravitee-Api-Key",
                            value = "dc1732ae-a8a0-4dd5-8052-26094bfbca11"
                        )
                    }
             */
        }  catch (e: RedirectResponseException) {
            // 3xx - responses
            Log.e("API_request 3xx", e.response.status.description)
            exitProcess(0)
        } catch (e: RedirectResponseException) {
            // 4xx - responses
            Log.e("API_request 4xx", e.response.status.description)
            exitProcess(0)
        } catch (e: ServerResponseException) {
            // 5xx - responses
            Log.e("API_request 5xx", e.response.status.description)
            exitProcess(0)
        } catch (e: Exception) {
            // General exception
            Log.e("API_request xxx", itempath)
            Log.e("API_request xxx", e.message.toString())
            exitProcess(0)
        }
        Log.d("API_request", "fetchLocationForecastData.launch success, response: ${response}")
        return response
    }


    suspend fun fetchMetAlertsData(url: String): MetAlertsResponse {
        Log.d("API_request", "attempting fetchMetAlertsData.launch")
        val response = try {
            client.get(url) {
                url(url)
                headers {
                    append(
                        name = "X-Gravitee-Api-Key",
                        value = "dc1732ae-a8a0-4dd5-8052-26094bfbca11"
                    )
                }
            }.body<MetAlertsResponse>()
        }  catch (e: RedirectResponseException) {
            // 3xx - responses
            Log.e("API_request 3xx", e.response.status.description)
            exitProcess(0)
        } catch (e: RedirectResponseException) {
            // 4xx - responses
            Log.e("API_request 4xx", e.response.status.description)
            exitProcess(0)
        } catch (e: ServerResponseException) {
            // 5xx - responses
            Log.e("API_request 5xx", e.response.status.description)
            exitProcess(0)
        } catch (e: Exception) {
            // General exception
            Log.e("API_request xxx", url)
            Log.e("API_request xxx", e.message.toString())
            exitProcess(0)
        }
        Log.d("API_request", "fetchMetAlertsData.launch success, response: ${response}")
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
package com.in2000_project.BoatApp.data

import android.util.Log
import com.in2000_project.BoatApp.model.seaOrLand.SeaOrLandResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.gson.*

class SeaOrLandDataSource {
    private val client = HttpClient{
        install(ContentNegotiation) {
            gson()
        }
    }
    suspend fun fetchSeaOrLand(path: String): SeaOrLandResponse{
        val ans = client.get(path).body<SeaOrLandResponse>()
        Log.i("SeaOrLandDataSource ", ans.toString())
        return ans
    }

}
package com.in2000_project.BoatApp.viewmodel

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlin.math.*

class OceanForecastViewModel: ViewModel() {
    // should find a way to know when it changes grid
    // take into account that I assume timeCheckingFor is given in minutes
    fun calculatePosition(coordinatesStart:List<Double>,
                          seaSurfaceWaveToDegrees: Double,
                          seaWaterSpeedInMeters: Double,
                          timeCheckingFor: Double
    ): LatLng {
        // Convert degrees to radians
        val waveFromInRadians = Math.toRadians(seaSurfaceWaveToDegrees)
        val earthRadiusInKm = 6371
        val startLatInRadians = Math.toRadians(coordinatesStart[0])
        val startLngInRadians = Math.toRadians(coordinatesStart[1])

        // Convert meters per second to kilometers per hour
        val waterSpeedInKmPerHour = seaWaterSpeedInMeters * 3.6

        // Convert the time interval to hours
        val timeIntervalInHours = timeCheckingFor / 60.0

        // Calculate the distance traveled by the object in the given time interval
        val distanceInKm = waterSpeedInKmPerHour * timeIntervalInHours

        // Calculate the new latitude and longitude
        val newLatInRadians = asin(sin(startLatInRadians) * cos(distanceInKm / earthRadiusInKm) + cos(startLatInRadians) * sin(distanceInKm / earthRadiusInKm) * cos(waveFromInRadians))
        val newLngInRadians = startLngInRadians + atan2(sin(waveFromInRadians) * sin(distanceInKm / earthRadiusInKm) * cos(startLatInRadians), cos(distanceInKm / earthRadiusInKm) - sin(startLatInRadians) * sin(newLatInRadians))

        // Convert the new latitude and longitude back to degrees
        val newLat = Math.toDegrees(newLatInRadians)
        val newLng = Math.toDegrees(newLngInRadians)

        return LatLng(newLat, newLng)
    }
    private fun test(){
        val start = listOf(59.026950, 10.413697)
        val degrees = 180.0
        val seaSpeed = 10.0
        val searchTime = 20.0
        println(calculatePosition(start, degrees, seaSpeed, searchTime))
    }
    init{
        test()
    }
}



package com.in2000_project.BoatApp.maps

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlin.math.*

/** finds the center of coordinates given in list */
fun List<LatLng>.getCenterOfPolygon(): LatLngBounds {
    val centerBuilder: LatLngBounds.Builder = LatLngBounds.builder()
    forEach { centerBuilder.include(LatLng(it.latitude, it.longitude)) }
    return centerBuilder.build()
}

/** finds the center of coordinates given in list:
    Takes in 2 coordinates, the position of the measurement from ocean forecast and the person overboard.
    Calculates the change in longitude and latitude. The abs function removes the sign of the number. abs(-30) = 30, abs(30) = 30.
    Calculates the number of meters between the longitude and latitude.
    Returns true if the person has drifted either 400m in longitude or in latitude.
 */
fun hasChangedGrid(dataCoordinate: LatLng, personCoordinate: LatLng): Boolean {
    // approximate radius of the Earth in meters
    val earthRadiusMeter = 6371000.0

    val deltaLongitude = abs(dataCoordinate.longitude - personCoordinate.longitude) * PI / 180
    val deltaLatitude = abs(dataCoordinate.latitude - personCoordinate.latitude) * PI / 180

    val distanceLongitude = 2 * PI * earthRadiusMeter * cos(personCoordinate.latitude * PI / 180) * deltaLongitude / 360
    val distanceLatitude = 2 * PI * earthRadiusMeter * deltaLatitude / 360

    val answer = distanceLongitude > 400.0 || distanceLatitude > 400.0
    Log.i("DriftCheck:", "person: $personCoordinate, data: $dataCoordinate. New grid = $answer")
    return answer
}


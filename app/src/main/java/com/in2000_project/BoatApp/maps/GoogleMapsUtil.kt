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
    val latDistancePerDegree = 111000 // approx distance in meters per degree of latitude, adjusted for the earths curvature
    val latdiff = abs(dataCoordinate.latitude - personCoordinate.latitude) * latDistancePerDegree

    val earthRadius = 6371e3 // Earth's radius in meters
    val latRad = Math.toRadians(dataCoordinate.latitude)
    val longdiff = abs(dataCoordinate.longitude - personCoordinate.longitude) * PI / 180 * cos(latRad) * earthRadius

    val answer = latdiff > 400.0 || longdiff > 400.0
    Log.i("DriftCheck:", "$dataCoordinate, $personCoordinate | data, person. New grid = $answer")
    return answer

}


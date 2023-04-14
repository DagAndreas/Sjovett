package com.in2000_project.BoatApp.maps

/**
 * A set of utility functions for centering the camera given some [LatLng] points.
 * Author: Mitch Tabian 2022
 */
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlin.math.*

fun List<LatLng>.getCenterOfPolygon(): LatLngBounds {
    val centerBuilder: LatLngBounds.Builder = LatLngBounds.builder()
    forEach { centerBuilder.include(LatLng(it.latitude, it.longitude)) }
    return centerBuilder.build()
}

private data class CameraViewCoord(
    val yMax: Double,
    val yMin: Double,
    val xMax: Double,
    val xMin: Double
)

fun List<LatLng>.calculateCameraViewPoints(pctView: Double = .25): List<LatLng> {
    val coordMax = findMaxMins()
    val dy = coordMax.yMax - coordMax.yMin
    val dx = coordMax.xMax - coordMax.xMin
    val yT = (dy * pctView) + coordMax.yMax
    val yB = coordMax.yMin - (dy * pctView)
    val xR = (dx * pctView) + coordMax.xMax
    val xL = coordMax.xMin - (dx * pctView)
    return listOf(
        LatLng(coordMax.xMax, yT),
        LatLng(coordMax.xMin, yB),
        LatLng(xR, coordMax.yMax),
        LatLng(xL, coordMax.yMin)
    )
}

private fun List<LatLng>.findMaxMins(): CameraViewCoord {
    check(size > 0) { "Cannot calculate the view coordinates of nothing." }
    var viewCoord: CameraViewCoord? = null
    for(point in this) {
        viewCoord = CameraViewCoord(
            yMax = viewCoord?.yMax?.let { yMax ->
                if (point.longitude > yMax) {
                    point.longitude
                } else {
                    yMax
                }
            } ?: point.longitude,
            yMin = viewCoord?.yMin?.let { yMin->
                if (point.longitude < yMin) {
                    point.longitude
                } else {
                    yMin
                }
            } ?: point.longitude,
            xMax = viewCoord?.xMax?.let { xMax->
                if (point.latitude > xMax) {
                    point.latitude
                } else {
                    xMax
                }
            } ?: point.latitude,
            xMin = viewCoord?.xMin?.let { xMin->
                if (point.latitude < xMin) {
                    point.latitude
                } else {
                    xMin
                }
            } ?: point.latitude,
        )
    }
    return viewCoord ?: throw IllegalStateException("viewCoord cannot be null.")
}


/**
 *tar inn 2 koordinater, posisjonen til målingen fra oceanforecast og person-overbord.
 * finner endringen fra lengdegraden og breddegraden. abs funksjonen fjerner fortegn på tall. abs (-30) = 30, abs(30) = 30.
 * beregner antall meter mellom lengdegrad og breddegrad.
 * returnerer true dersom personen har driftet enten 400m i lengdegrad eller i breddegrad.
 */

fun personHarDriftetTilNesteGrid(dataCoordinate: LatLng, personCoordinate: LatLng): Boolean {
    val jordaRadiusMeter = 6371000.0 // approximate radius of the Earth in meters

    val deltaLengdegrad = abs(dataCoordinate.longitude - personCoordinate.longitude)
    val deltaBreddegrad = abs(dataCoordinate.latitude - personCoordinate.latitude)

    val avstandLengdegrad = 2 * PI * jordaRadiusMeter * cos(personCoordinate.latitude * PI / 180) * deltaLengdegrad / 360
    val avstandBreddegrad = 2 * PI * jordaRadiusMeter * deltaBreddegrad / 360

    val svar = avstandLengdegrad > 400.0 || avstandBreddegrad > 400.0
    Log.i("Driftsjekk:", "person: $personCoordinate, data: $dataCoordinate. Har byttet? = $svar")
    return svar
}



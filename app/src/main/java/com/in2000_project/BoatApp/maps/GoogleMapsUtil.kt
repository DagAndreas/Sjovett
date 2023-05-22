package com.in2000_project.BoatApp.maps

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlin.math.*

fun List<LatLng>.getCenterOfPolygon(): LatLngBounds {
    val centerBuilder: LatLngBounds.Builder = LatLngBounds.builder()
    forEach { centerBuilder.include(LatLng(it.latitude, it.longitude)) }
    return centerBuilder.build()
}

/**
 *tar inn 2 koordinater, posisjonen til målingen fra oceanforecast og person-overbord.
 * finner endringen fra lengdegraden og breddegraden. abs funksjonen fjerner fortegn på tall. abs (-30) = 30, abs(30) = 30.
 * beregner antall meter mellom lengdegrad og breddegrad.
 * returnerer true dersom personen har driftet enten 400m i lengdegrad eller i breddegrad.
 */

fun personHarDriftetTilNesteGrid(dataCoordinate: LatLng, personCoordinate: LatLng): Boolean {
    val earthRadiusMeter = 6371000.0 // approximate radius of the Earth in meters

    val deltaLongitude = abs(dataCoordinate.longitude - personCoordinate.longitude) * PI / 180
    val deltaLatitude = abs(dataCoordinate.latitude - personCoordinate.latitude) * PI / 180

    val distanceLongitude = 2 * PI * earthRadiusMeter * cos(personCoordinate.latitude * PI / 180) * deltaLongitude / 360
    val distanceLatitude = 2 * PI * earthRadiusMeter * deltaLatitude / 360

    val answer = distanceLongitude > 400.0 || distanceLatitude > 400.0
    Log.i("DriftCheck:", "person: $personCoordinate, data: $dataCoordinate. New grid = $answer")
    return answer
}


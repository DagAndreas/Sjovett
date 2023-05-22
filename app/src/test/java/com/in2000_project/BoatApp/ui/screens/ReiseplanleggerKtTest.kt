package com.in2000_project.BoatApp.ui.screens

import org.junit.Assert.*

import org.junit.Test

class ReiseplanleggerKtTest() {
    @Test
    fun fromAtoB() {
        // TODO: Arrange
        val distanceInMeters = 1853.0
        val knots = 100f

        // TODO: Act
        val outcome = (calculateTimeInMinutes(distanceInMeters, knots) == 0.6)

        //TODO: Assert

        assertTrue(outcome)
    }
    @Test
    fun formatMinutes1() {
        // TODO: Arrange
        val distanceInMeters = 1853.0
        val knots = 100f

        // TODO: Act
        val outcome = (formatTime(calculateTimeInMinutes(distanceInMeters, knots)) == "Under 1 minutt")

        //TODO: Assert
        assertTrue(outcome)
    }

    @Test
    fun formatMinutes2() {
        // TODO: Arrange
        val distanceInMeters = 1853.0
        val knots = 50f

        // TODO: Act
        val outcome = (formatTime(calculateTimeInMinutes(distanceInMeters, knots)) == "1 minutter")

        //TODO: Assert
        assertTrue(outcome)
    }
}













package com.in2000_project.BoatApp.view.components.mann_over_bord

import android.util.Log
import com.in2000_project.BoatApp.viewmodel.MapViewModel

class MapUpdateThread(
    private val mapViewModel: MapViewModel
) : Thread() {
    var isRunning = false
    override fun run() {
        Log.i("thread start pos", "${mapViewModel.circleCenter.value}")
        isRunning = true
        sleep(800) // Sleeps to ensure that data has been collected from oceanforecastobject
        val sleepDelay: Long = 2 // seconds
        while (isRunning) {
            // sleepDelay counts the seconds between updates, sleepDelay*30 will simulate 60 seconds every 2 seconds
            mapViewModel.updateMap(sleepDelay)
            mapViewModel.updateMarkerAndPolyLines()
            // in milliseconds, this function waits 2 seconds between each update
            sleep(sleepDelay * 1000)
        }
        isRunning = false
    }
}

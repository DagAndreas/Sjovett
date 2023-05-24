package com.in2000_project.BoatApp.launch

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.in2000_project.BoatApp.MapActivity
import com.in2000_project.BoatApp.R


class NoUserLocation : AppCompatActivity() {
    private fun checkLocationPermission(): Boolean {
        val fineLocationPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocationPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                coarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
            }
        }

    private fun askPermissions() = when {
    ContextCompat.checkSelfPermission(
        this,
        ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED -> {

    }
    else -> {
        requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
    }
}

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("HAR DU SAMTYKKE?", "Spurte du?")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_user_location)

        askPermissions()

        Log.e("HAR DU SAMTYKKE?", "Gjorde jeg vel")

        while (!checkLocationPermission()){
            /* Waiting patiently */
        }

        Log.e("HAR DU SAMTYKKE?", "Ja nå har du det, din gris")

        startActivity(Intent(this, MapActivity::class.java))
        finish()
    }
}
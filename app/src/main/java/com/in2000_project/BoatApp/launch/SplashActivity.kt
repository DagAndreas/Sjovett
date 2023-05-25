package com.in2000_project.BoatApp.launch

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.core.content.ContextCompat
import com.in2000_project.BoatApp.MainActivity
import com.in2000_project.BoatApp.R

class SplashActivity : AppCompatActivity() {
    private val SPLASH_DELAY: Long = 3000 // 3 seconds

    private fun checkLocationPermission(): Boolean {
        val fineLocationPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocationPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                coarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val internet = CheckInternet(cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager)
        Log.e("HAR DU SAMTYKKE?", "${checkLocationPermission()}")
        // Delay the start of MainActivity using a Handler
        Handler().postDelayed({
            val intent = if (!internet.checkNetwork()) {
                Log.d("HAR DU IKKE INTERNETT?", "hahhah")
                Intent(this, NoNetworkActivity::class.java)
            } else if (!checkLocationPermission()) {
                Log.d("HAR DU IKKE SAMTYKKE?", "fette")
                Intent(this, NoUserLocation::class.java)
            } else {
                Intent(this, MainActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, SPLASH_DELAY)
    }
}
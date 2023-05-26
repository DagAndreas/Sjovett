package com.in2000_project.BoatApp.launch

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.in2000_project.BoatApp.MainActivity
import com.in2000_project.BoatApp.R

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val SPLASH_DELAY: Long = 3000 // 3 seconds

    private fun checkLocationPermission(): Boolean {
        val fineLocationPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocationPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fineLocationPermission == PackageManager.PERMISSION_GRANTED && coarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val internet =
            CheckInternet(cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager)
        // Delay the start of MainActivity using a Handler
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = if (!internet.checkNetwork()) {
                Intent(this, NoNetworkActivity::class.java)
            } else if (!checkLocationPermission()) {
                Intent(this, NoUserLocation::class.java)
            } else {
                Intent(this, MainActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, SPLASH_DELAY)
    }
}
package com.in2000_project.BoatApp

import Drawer
import Sjoemerkesystemet
import Sjovettreglene
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.in2000_project.BoatApp.ui.screens.Livredning
import com.in2000_project.BoatApp.ui.screens.MannOverbord
import com.in2000_project.BoatApp.ui.screens.StormWarning
import com.in2000_project.BoatApp.ui.screens.TidsbrukScreen
import com.in2000_project.BoatApp.viewmodel.*
import com.plcoding.bottomnavwithbadges.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*


// Heisann og hoppsan
@AndroidEntryPoint
class MapActivity : ComponentActivity() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val viewModel: MapViewModel by viewModels()

    private val alertsMapViewModel = AlertsMapViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        viewModel.setClient(fusedLocationProviderClient)

        setContent {
            window.statusBarColor = ContextCompat.getColor(this, R.color.black)

            AppTheme {
                val navController = rememberNavController()
                val stormWarningViewModels = MetAlertsViewModel()
                val temperatureViewModel = LocationForecastViewModel()
                val searchViewModel = SearchViewModel()
                val internet = CheckInternet(cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager)

                Surface(color = MaterialTheme.colors.background) {
                    val drawerState = rememberDrawerState(DrawerValue.Closed)
                    val scope = rememberCoroutineScope()
                    val openDrawer = {
                        scope.launch {
                            drawerState.open()
                        }
                    }

                    ModalDrawer(
                        drawerState = drawerState,
                        gesturesEnabled = drawerState.isOpen,
                        drawerContent = {
                            Drawer(
                                onDestinationClicked = { route ->
                                    scope.launch {
                                        drawerState.close()
                                    }
                                    navController.navigate(route) {
                                        launchSingleTop = true
                                    }
                                },
                                navController = navController
                            )
                        },
                        drawerShape = RoundedCornerShape(topEnd = 30.dp, bottomEnd = 30.dp)
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = DrawerScreens.MannOverBord.route
                        ) {
                            composable(DrawerScreens.MannOverBord.route) {
                                MannOverbord(
                                    mapViewModel = viewModel,
                                    openDrawer = {
                                        openDrawer()
                                    },
                                    connection = internet
                                )
                            }
                            composable(DrawerScreens.StormWarning.route) {
                                StormWarning(
                                    viewModelAlerts = stormWarningViewModels,
                                    viewModelForecast = temperatureViewModel,
                                    viewModelMap = alertsMapViewModel,
                                    viewModelSearch = searchViewModel,
                                    setupClusterManager = alertsMapViewModel::setupClusterManager,
                                    modifier = Modifier,
                                    openDrawer = {
                                        openDrawer()
                                    },
                                    connection = internet
                                )
                            }
                            composable(DrawerScreens.TidsbrukScreen.route) {
                                TidsbrukScreen(
                                    viewModel = viewModel,
                                    openDrawer = {
                                        openDrawer()
                                    }
                                )
                            }

                            composable(DrawerScreens.Livredning.route) {
                                Livredning(
                                    openDrawer = {
                                        openDrawer()
                                    }
                                )
                            }

                            composable(DrawerScreens.Sjomerkesystemet.route) {
                                Sjoemerkesystemet(
                                    openDrawer = {
                                        openDrawer()
                                    }
                                )
                            }

                            composable(DrawerScreens.Sjovettreglene.route) {
                                Sjovettreglene(
                                    openDrawer = {
                                        openDrawer()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


sealed class DrawerScreens(val title: String, val route: String) {
    object MannOverBord : DrawerScreens("Mann over bord", "mannoverbord")
    object StormWarning : DrawerScreens("Stormvarsel", "stormvarsel")
    object TidsbrukScreen : DrawerScreens( "Reiseplanlegger", "reiseplanlegger")
    object Livredning : DrawerScreens("Livredning", "livredning")
    object Sjomerkesystemet : DrawerScreens("Sjømerkesystemet", "sjomerkesystemet")
    object Sjovettreglene : DrawerScreens("Sjøvettreglene", "sjovettreglene")
}




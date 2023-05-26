package com.in2000_project.BoatApp

import Menu
import Sjoemerkesystemet
import Sjovettreglene
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.in2000_project.BoatApp.launch.CheckInternet
import com.in2000_project.BoatApp.launch.InternetPopupState
import com.in2000_project.BoatApp.view.screens.Livredning
import com.in2000_project.BoatApp.view.screens.MannOverbord
import com.in2000_project.BoatApp.view.screens.Stormvarsel
import com.in2000_project.BoatApp.view.screens.TidsbrukScreen
import com.in2000_project.BoatApp.viewmodel.*
import com.plcoding.bottomnavwithbadges.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val viewModel: MapViewModel by viewModels()
    private val alertsMapViewModel = AlertsMapViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        viewModel.setClient(fusedLocationProviderClient)
        alertsMapViewModel.setClient(fusedLocationProviderClient)
        alertsMapViewModel.updateLocation()

        setContent {
            window.statusBarColor = ContextCompat.getColor(this, R.color.black)

            AppTheme {
                val navController = rememberNavController()
                val stormvarselViewModels = MetAlertsViewModel()
                val temperatureViewModel = LocationForecastViewModel()
                val context = LocalContext.current
                val searchViewModel = SearchViewModel(context)
                val internet =
                    CheckInternet(cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager)
                val internetPopupState = InternetPopupState()

                Surface(color = MaterialTheme.colors.background) {
                    val menuState = rememberDrawerState(DrawerValue.Closed)
                    val scope = rememberCoroutineScope()
                    val openMenu = {
                        scope.launch {
                            menuState.open()
                        }
                    }

                    ModalDrawer(
                        drawerState = menuState,
                        gesturesEnabled = menuState.isOpen,
                        drawerContent = {
                            Menu(
                                onDestinationClicked = { route ->
                                    scope.launch {
                                        menuState.close()
                                    }
                                    navController.navigate(route) {
                                        launchSingleTop = true
                                    }
                                }, navController = navController
                            )
                        },
                        drawerShape = RoundedCornerShape(topEnd = 30.dp, bottomEnd = 30.dp)
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = MenuScreens.MannOverBord.route
                        ) {
                            composable(MenuScreens.MannOverBord.route) {
                                MannOverbord(
                                    mapViewModel = viewModel,
                                    openMenu = {
                                        openMenu()
                                    },
                                    connection = internet,
                                    internetPopupState = internetPopupState
                                )
                            }
                            composable(MenuScreens.Stormvarsel.route) {
                                Stormvarsel(
                                    viewModelAlerts = stormvarselViewModels,
                                    viewModelForecast = temperatureViewModel,
                                    viewModelMap = alertsMapViewModel,
                                    viewModelSearch = searchViewModel,
                                    setupClusterManager = alertsMapViewModel::setupClusterManager,
                                    modifier = Modifier,
                                    openMenu = {
                                        openMenu()
                                    },
                                    connection = internet,
                                    internetPopupState = internetPopupState
                                )
                            }
                            composable(MenuScreens.TidsbrukScreen.route) {
                                TidsbrukScreen(viewModel = viewModel, openMenu = {
                                    openMenu()
                                })
                            }

                            composable(MenuScreens.Livredning.route) {
                                Livredning(openMenu = {
                                    openMenu()
                                })
                            }

                            composable(MenuScreens.Sjomerkesystemet.route) {
                                Sjoemerkesystemet(openMenu = {
                                    openMenu()
                                })
                            }

                            composable(MenuScreens.Sjovettreglene.route) {
                                Sjovettreglene(openMenu = {
                                    openMenu()
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}


sealed class MenuScreens(val title: String, val route: String) {
    object MannOverBord : MenuScreens("Mann over bord", "mannoverbord")
    object Stormvarsel : MenuScreens("Stormvarsel", "stormvarsel")
    object TidsbrukScreen : MenuScreens("Reiseplanlegger", "reiseplanlegger")
    object Livredning : MenuScreens("Livredning", "livredning")
    object Sjomerkesystemet : MenuScreens("Sjømerkesystemet", "sjomerkesystemet")
    object Sjovettreglene : MenuScreens("Sjøvettreglene", "sjovettreglene")
}




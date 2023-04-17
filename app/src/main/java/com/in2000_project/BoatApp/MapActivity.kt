package com.in2000_project.BoatApp

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.in2000_project.BoatApp.compose.MapScreen
import com.in2000_project.BoatApp.compose.TidsbrukScreen
import com.in2000_project.BoatApp.ui.BottomNavItem
import com.in2000_project.BoatApp.ui.screens.StormWarning
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.in2000_project.BoatApp.ui.screens.VerktoyScreen
import com.in2000_project.BoatApp.viewmodel.*
import com.plcoding.bottomnavwithbadges.ui.theme.BottomNavWithBadgesTheme
import dagger.hilt.android.AndroidEntryPoint


// Heisann og hoppsan
@AndroidEntryPoint
class MapActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.getDeviceLocation(fusedLocationProviderClient)
                alertsMapViewModel.getDeviceLocation(fusedLocationProviderClient)
            }
        }

    private fun askPermissions() = when {
        ContextCompat.checkSelfPermission(
            this,
            ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED -> {
            viewModel.getDeviceLocation(fusedLocationProviderClient)
            alertsMapViewModel.getDeviceLocation(fusedLocationProviderClient)

        }
        else -> {
            requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
        }
    }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val viewModel: MapViewModel by viewModels()

    private val alertsMapViewModel = AlertsMapViewModel()

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        viewModel.setClient(fusedLocationProviderClient)
        askPermissions()
        setContent {

            BottomNavWithBadgesTheme {
                val navController = rememberNavController()

                Column() {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(0.912F)
                    ) {
                        Navigation(navController = navController, viewModel = viewModel, alertsMapViewModel = alertsMapViewModel)
                    }

                    BottomNavigationBar(
                        items = listOf(
                            BottomNavItem(
                                name = "Kart",
                                route = "kart",
                                icon = Icons.Default.Map,
                            ),
                            BottomNavItem(
                                name = "Været",
                                route = "været",
                                icon = Icons.Default.WbSunny,

                                ),
                            BottomNavItem(
                                name = "Tidsbruk",
                                route = "tidsbruk",
                                icon = Icons.Default.Timer,

                                ),
                            BottomNavItem(
                                name = "Verktøy",
                                route = "verktoy",
                                icon = Icons.Default.Settings,

                                ),
                        ),
                        navController = navController,
                        onItemClick = {
                            navController.navigate(it.route)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Navigation(navController: NavHostController, viewModel: MapViewModel, alertsMapViewModel: AlertsMapViewModel) {
    NavHost(navController = navController, startDestination = "kart") {
        composable("kart") {
            MapScreen(viewModel = viewModel)
        }
        composable("været") {
            val stormWarningViewModels = MetAlertsViewModel()
            val temperatureViewModel = LocationForecastViewModel()
            val searchViewModel = SearchViewModel()
            StormWarning(
                stormWarningViewModels,
                temperatureViewModel,
                alertsMapViewModel,
                searchViewModel,
                setupClusterManager = alertsMapViewModel::setupClusterManager,
                calculateZoneViewCenter = alertsMapViewModel::calculateZoneLatLngBounds,
                modifier = Modifier)
        }
        composable("tidsbruk") {
            TidsbrukScreen(viewModel = viewModel)
        }
        composable("verktoy") {
            VerktoyScreen()
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun BottomNavigationBar(
    items: List<BottomNavItem>,
    navController: NavController,
    modifier: Modifier = Modifier,
    onItemClick: (BottomNavItem) -> Unit
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    BottomNavigation(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .padding(1.dp),
        backgroundColor = Color.White,
        elevation = 5.dp,


        ) {
        items.forEach { item ->
            val selected = item.route == backStackEntry.value?.destination?.route
            BottomNavigationItem(
                selected = selected,
                onClick = { onItemClick(item) },
                selectedContentColor = Color.Gray,
                unselectedContentColor = Color.LightGray,
                icon = {
                    Column(horizontalAlignment = CenterHorizontally) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.name
                        )
                        if (selected) {
                            Text(
                                text = item.name,
                                textAlign = TextAlign.Center,
                                fontSize = 10.sp,
                            )}
                    }
                }
            )
        }
    }
}

@Composable
fun KartScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Kart")
    }
}

@Composable
fun VaeretScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Været")
    }
}




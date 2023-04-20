package com.in2000_project.BoatApp

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DirectionsBoat
import androidx.compose.material.icons.outlined.Support
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material.icons.rounded.Support
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
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
import com.in2000_project.BoatApp.compose.MannOverbord
import com.in2000_project.BoatApp.compose.TidsbrukScreen
import com.in2000_project.BoatApp.ui.BottomNavItem
import com.in2000_project.BoatApp.ui.screens.StormWarning
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.in2000_project.BoatApp.ui.screens.VerktoyScreen
import com.in2000_project.BoatApp.viewmodel.*
import com.plcoding.bottomnavwithbadges.ui.theme.BottomNavWithBadgesTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


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

    @RequiresApi(Build.VERSION_CODES.O)
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

                val stormWarningViewModels = MetAlertsViewModel()
                val temperatureViewModel = LocationForecastViewModel()
                val searchViewModel = SearchViewModel()

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
                                    }
                                )
                            }
                            composable(DrawerScreens.StormWarning.route) {
                                StormWarning(
                                    viewModelAlerts = stormWarningViewModels,
                                    viewModelForecast = temperatureViewModel,
                                    viewModelMap = alertsMapViewModel,
                                    viewModelSearch = searchViewModel,
                                    setupClusterManager = alertsMapViewModel::setupClusterManager,
                                    calculateZoneViewCenter = alertsMapViewModel::calculateZoneLatLngBounds,
                                    modifier = Modifier,
                                    openDrawer = {
                                        openDrawer()
                                    }
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
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Drawer(
    modifier: Modifier = Modifier,
    onDestinationClicked: (route: String) -> Unit,
    navController: NavController
) {
    val screenMap: Map<DrawerScreens, ImageVector> = mapOf(
        DrawerScreens.MannOverBord to Icons.Outlined.Support,
        DrawerScreens.StormWarning to Icons.Outlined.WbSunny,
        DrawerScreens.TidsbrukScreen to Icons.Rounded.Timer
    )

    Column(
        modifier
            .fillMaxSize()
            .padding(top = 48.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "App icon",
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .padding(start = 24.dp)
        )

        Spacer(Modifier.height(24.dp))

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        screenMap.forEach { screen ->
            val selected = currentRoute == screen.key.route
            val background = if (selected) Color.LightGray else Color.Unspecified

            Row(
                modifier = modifier
                    .background(
                        color = background,
                        shape = RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp)
                    )
                    .height(40.dp)
                    .fillMaxWidth(0.62f)
                    .padding(start = 24.dp)
            ) {
                Icon(
                    imageVector = screen.value,
                    contentDescription = "drawerIcon",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )

                Text(
                    text = screen.key.title,
                    style = MaterialTheme.typography.h4,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 8.dp)
                        .clickable {
                            onDestinationClicked(screen.key.route)
                        },
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun MenuButton(title: String = "", buttonIcon: ImageVector, onButtonClicked: () -> Unit) {

    IconButton(
        onClick = { onButtonClicked() } ,
        /*
        modifier = Modifier
            .size(LocalConfiguration.current.screenWidthDp.dp * 0.16f)
            .background(
                color = Color.Unspecified,
                shape = CircleShape
            )

         */

    ) {
        Icon(
            imageVector = buttonIcon,
            contentDescription = "",
            modifier = Modifier
                .background(
                    color = Color.White,
                    shape = CircleShape
                )
                .padding(10.dp)
                .size(LocalConfiguration.current.screenWidthDp.dp * 0.07f)
        )
    }
}

sealed class DrawerScreens(val title: String, val route: String) {
    object MannOverBord : DrawerScreens("Mann over bord", "mannoverbord")
    object StormWarning : DrawerScreens("Stormvarsel", "stormvarsel")
    object TidsbrukScreen : DrawerScreens( "Reiseplanlegger", "reiseplanlegger")
}




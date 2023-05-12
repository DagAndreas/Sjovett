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
import com.in2000_project.BoatApp.DrawerScreens
import com.in2000_project.BoatApp.R
import com.in2000_project.BoatApp.ui.screens.VerktoyScreen
import com.in2000_project.BoatApp.viewmodel.*
import com.plcoding.bottomnavwithbadges.ui.theme.BottomNavWithBadgesTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


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
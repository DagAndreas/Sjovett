import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.in2000_project.BoatApp.MenuScreens
import com.in2000_project.BoatApp.R
import com.plcoding.bottomnavwithbadges.ui.theme.LightGrey
import com.plcoding.bottomnavwithbadges.ui.theme.Transparent


@Composable
fun Menu(
    modifier: Modifier = Modifier,
    onDestinationClicked: (route: String) -> Unit,
    navController: NavController
) {
    val screenMap: Map<MenuScreens, ImageVector> = mapOf(
        MenuScreens.MannOverBord to Icons.Outlined.Support,
        MenuScreens.Stormvarsel to Icons.Outlined.WbSunny,
        MenuScreens.TidsbrukScreen to Icons.Rounded.Timer,
        MenuScreens.Livredning to Icons.Outlined.MedicalServices,
        MenuScreens.Sjomerkesystemet to Icons.Outlined.Book,
        MenuScreens.Sjovettreglene to Icons.Outlined.List
    )

    Column(
        modifier
            .fillMaxSize()
            .padding(top = 48.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = stringResource(R.string.Logo),
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .padding(start = 24.dp)
        )

        Spacer(Modifier.height(24.dp))

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        screenMap.forEach { screen ->
            val selected = currentRoute == screen.key.route
            val background = if (selected) LightGrey else Transparent

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
                    contentDescription = stringResource(R.string.Menu),
                    modifier = Modifier.align(Alignment.CenterVertically)
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
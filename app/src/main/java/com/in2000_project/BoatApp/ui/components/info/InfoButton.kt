import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.in2000_project.BoatApp.R
import com.in2000_project.BoatApp.viewmodel.AlertsMapViewModel
import com.in2000_project.BoatApp.viewmodel.MapViewModel
import com.plcoding.bottomnavwithbadges.ui.theme.Black
import com.plcoding.bottomnavwithbadges.ui.theme.White

@Composable
fun InfoButton(
    mapViewModel: MapViewModel,
    screen: String
) {
    IconButton(
        onClick = {
            if (screen == "Reiseplanlegger") {
                mapViewModel.reiseplanleggerInfoPopUp = true
            }
            else if (screen == "Mann-over-bord") {
                mapViewModel.mannOverBordInfoPopUp = true
            }
        },
        modifier = Modifier.padding(top = 2.dp)

    ) {
        Icon(
            Icons.Outlined.Info,
            contentDescription = stringResource(R.string.Informasjon),
            modifier = Modifier
                .size(32.dp),
            tint = White
        )
    }
}

@Composable
fun InfoButtonStorm(
    alertsMapViewModel: AlertsMapViewModel
) {
    IconButton(
        onClick = {
            alertsMapViewModel.stormvarselInfoPopUp = true
                  }
    ) {
        Icon(
            Icons.Outlined.Info,
            contentDescription = stringResource(R.string.Informasjon),
            modifier = Modifier
                .size(32.dp),
            tint = Black
        )
    }
}
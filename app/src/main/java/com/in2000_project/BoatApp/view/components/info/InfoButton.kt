import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.in2000_project.BoatApp.R
import com.in2000_project.BoatApp.viewmodel.AlertsMapViewModel
import com.in2000_project.BoatApp.viewmodel.MapViewModel
import com.plcoding.bottomnavwithbadges.ui.theme.Black
import com.plcoding.bottomnavwithbadges.ui.theme.White

/** Represents the information buttons in the top left corner of the screens */
@Composable
fun InfoButton(
    mapViewModel: MapViewModel, screen: String
) {
    IconButton(
        onClick = {
            //
            when (screen) {
                "Reiseplanlegger" -> {
                    mapViewModel.reiseplanleggerInfoPopup = true
                }
                "Mann-over-bord" -> {
                    mapViewModel.manIsOverboardInfoPopup = true
                }
            }
        },
        modifier = Modifier
            .size(LocalConfiguration.current.screenWidthDp.dp * 0.115f)
            .background(
                color = White, shape = CircleShape
            ),
    ) {
        Icon(
            Icons.Outlined.Info,
            contentDescription = stringResource(R.string.Information),
            modifier = Modifier
                .size(LocalConfiguration.current.screenWidthDp.dp * 0.07f)
                .background(
                    color = White, shape = CircleShape
                ),
            tint = Black
        )
    }
}

@Composable
fun InfoButtonStorm(
    alertsMapViewModel: AlertsMapViewModel
) {
    IconButton(onClick = {
        alertsMapViewModel.stormvarselInfoPopUp = true
    }) {
        Icon(
            Icons.Outlined.Info,
            contentDescription = stringResource(R.string.Information),
            modifier = Modifier.size(32.dp),
            tint = Black
        )
    }
}
package com.in2000_project.BoatApp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.ViewModel
import com.in2000_project.BoatApp.R
import com.in2000_project.BoatApp.viewmodel.AlertsMapViewModel
import com.in2000_project.BoatApp.viewmodel.MapViewModel

@Composable
fun InfoPopup(
    mapViewModel: MapViewModel,
    screen: String
) {

    AlertDialog(
        onDismissRequest = {
            if (screen == "Reiseplanlegger") {
                mapViewModel.reiseplanleggerInfoPopUp = false
            }
            else if (screen == "Mann-over-bord") {
                mapViewModel.mannOverBordInfoPopUp = false
            }
        },
        title = { Text("Informasjon") },
        text = { Text(
            if (screen == "Reiseplanlegger") {
                mapViewModel.infoTextReiseplanlegger
            }
            else if (screen == "Mann-over-bord") {
                mapViewModel.infoTextMannOverBord
            }
            else {
                ""
            }
        )
               }, //kan legges som String resource
        buttons = { }
    )
}

@Composable
fun InfoPopupStorm(
    alertsMapViewModel: AlertsMapViewModel
) {
    AlertDialog(
        onDismissRequest = {
            alertsMapViewModel.stormvarselInfoPopUp = false
        },
        title = { Text(stringResource(R.string.Informasjon)) },
        text = { Text(alertsMapViewModel.infoTextStormvarsel) }, //kan legges som String resource
        buttons = { }
    )
}
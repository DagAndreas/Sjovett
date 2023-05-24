package com.in2000_project.BoatApp.ui.components

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
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
                mapViewModel.manIsOverboardInfoPopup = false
            }
        },
        title = { Text("Informasjon") },
        text = { Text(
            if (screen == "Reiseplanlegger") {
                // TODO: Sander T, ha en sjekk her for hvilken tekst som skal vises
                mapViewModel.infoTextReiseplanlegger
            }
            else if (screen == "Mann-over-bord") {
                mapViewModel.infoTextMannOverBord
            }
            else {
                ""
            }
        )},
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
        title = { Text(stringResource(R.string.Information)) },
        text = { Text(alertsMapViewModel.infoTextStormvarsel) }, //kan legges som String resource
        buttons = { }
    )
}
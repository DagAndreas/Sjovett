package com.in2000_project.BoatApp.view.components

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.in2000_project.BoatApp.R
import com.in2000_project.BoatApp.viewmodel.AlertsMapViewModel
import com.in2000_project.BoatApp.viewmodel.MapViewModel

/** Represents the information Popup that is displayed when the information button in the top left corner is pushed */
@Composable
fun InfoPopup(
    mapViewModel: MapViewModel, screen: String
) {
    val infoTextMannOverBord = stringResource(R.string.InfoTextMannOverBord)
    val infoTextReiseplanlegger = stringResource(R.string.InfoTextReiseplanlegger)

    AlertDialog(onDismissRequest = {
        if (screen == "Reiseplanlegger") {
            mapViewModel.reiseplanleggerInfoPopup = false
        } else if (screen == "Mann-over-bord") {
            mapViewModel.manIsOverboardInfoPopup = false
        }
    }, title = { Text("Informasjon") }, text = {
        Text(
            if (screen == "Reiseplanlegger") {
                infoTextReiseplanlegger
            } else if (screen == "Mann-over-bord") {
                infoTextMannOverBord
            } else {
                ""
            }
        )
    }, buttons = { })
}

@Composable
fun InfoPopupStorm(
    alertsMapViewModel: AlertsMapViewModel
) {

    val infoTextStormVarsel = stringResource(R.string.InfoTextStormvarsel)

    AlertDialog(onDismissRequest = {
        alertsMapViewModel.stormvarselInfoPopUp = false
    },
        title = { Text(stringResource(R.string.Information)) },
        text = { Text(infoTextStormVarsel) },
        buttons = { })
}
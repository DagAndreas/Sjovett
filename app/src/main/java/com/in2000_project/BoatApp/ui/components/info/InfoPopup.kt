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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.ViewModel
import com.in2000_project.BoatApp.viewmodel.AlertsMapViewModel
import com.in2000_project.BoatApp.viewmodel.MapViewModel

@Composable
fun InfoPopup(
    mapViewModel: MapViewModel,
    screen: String
) {
    /*
    Popup(
        alignment = Alignment.Center,
        properties = PopupProperties(
            focusable = true
        )

    ) {
        ElevatedCard(
            modifier = Modifier
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp)
                )
                .width(LocalConfiguration.current.screenWidthDp.dp * 0.6f)
                .height(LocalConfiguration.current.screenHeightDp.dp * 0.15f)
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                IconButton(
                    onClick = {
                        if (screen == "Reiseplanlegger") {
                            mapViewModel.reiseplanleggerInfoPopUp = false
                        }
                        else if (screen == "Mann-over-bord") {
                            mapViewModel.mannOverBordInfoPopUp = false
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.End)
                ) {
                    Icon(
                        Icons.Outlined.Close,
                        contentDescription = "Close",
                        modifier = Modifier
                            .size(24.dp),
                        tint = Color.Gray
                    )
                }

                Text(
                    text = mapViewModel.infoTextMannOverBord,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
     */
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
        title = { Text("Informasjon") },
        text = { Text(alertsMapViewModel.infoTextStormvarsel) }, //kan legges som String resource
        buttons = { }
    )
}
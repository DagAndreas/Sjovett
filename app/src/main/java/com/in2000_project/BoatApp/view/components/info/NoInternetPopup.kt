package com.in2000_project.BoatApp.view.components.info

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.in2000_project.BoatApp.launch.InternetPopupState

/** Shows a Popup that the device does not have Internet connection */
@Composable
fun NoInternetPopup(
    internetPopupState: InternetPopupState
){
    AlertDialog(
        onDismissRequest = {
            internetPopupState.checkInternetPopup.value = false
        },
        title = { Text("Ikke internett") },
        text = { Text(
            text = "Det ser ut som du ikke har internett. Koble til internett for å bruke denne funksjonen."
        )
        },
        buttons = {}
    )
}
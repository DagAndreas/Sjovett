package com.in2000_project.BoatApp.ui.components.info

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.in2000_project.BoatApp.launch.InternetPopupState

//TODO: Her trenger vi en popup som sier til brukeren at de ikke er koblet til nett

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
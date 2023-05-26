package com.in2000_project.BoatApp.view.components.info

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.in2000_project.BoatApp.R
import com.in2000_project.BoatApp.launch.InternetPopupState

/** Shows a Popup that the device does not have Internet connection */
@Composable
fun NoInternetPopup(
    internetPopupState: InternetPopupState
) {
    AlertDialog(onDismissRequest = {
        internetPopupState.checkInternetPopup.value = false
    }, title = { Text(stringResource(R.string.NoInternet)) }, text = {
        Text(
            text = stringResource(R.string.NoInternetMessage)
        )
    }, buttons = {})
}
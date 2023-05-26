package com.in2000_project.BoatApp.view.components.mann_over_bord

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.in2000_project.BoatApp.R
import com.in2000_project.BoatApp.viewmodel.MapViewModel
import com.plcoding.bottomnavwithbadges.ui.theme.Black
import com.plcoding.bottomnavwithbadges.ui.theme.White


/** Represents the Popup that is displayed when the user tries to stop the search in Mann-over-bord */
@Composable
fun StopSearchPopup(
    mapViewModel: MapViewModel
) {
    AlertDialog(onDismissRequest = { mapViewModel.showDialog = false },
        title = { Text(stringResource(R.string.AreYouSure)) },
        text = { Text(stringResource(R.string.AreYouSureText)) },
        buttons = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp, start = 8.dp, end = 20.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { mapViewModel.showDialog = false },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Black, backgroundColor = White
                    ),
                    shape = RoundedCornerShape(5.dp),
                    border = BorderStroke(width = 1.dp, color = Black)
                ) {
                    Text(stringResource(R.string.No))
                }
                val buttonText = stringResource(R.string.StartSearch)

                Button(
                    onClick = {
                        mapViewModel.stopSearchPopupYes(buttonText)
                    },
                    modifier = Modifier.padding(start = 20.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = White, backgroundColor = Black
                    ),
                    shape = RoundedCornerShape(5.dp),
                    border = BorderStroke(width = 1.dp, color = Black)
                ) {
                    Text(stringResource(R.string.Yes))
                }
            }
        })
}



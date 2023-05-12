import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.in2000_project.BoatApp.viewmodel.MapViewModel

@Composable
fun AvsluttSokPopup(
    mapViewModel: MapViewModel
) {
    AlertDialog(
        onDismissRequest = { mapViewModel.showDialog = false },
        title = { Text("Er du sikker? ") },
        text = { Text("Du er nå i ferd med å stoppe søking. \nVil  du avslutte?") }, //kan legges som String resource
        buttons = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp, start = 8.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { mapViewModel.showDialog = false }
                ) {
                    Text("Nei")
                }

                Button(
                    onClick = {
                        mapViewModel.showDialog = false
                        mapViewModel.restartButton()
                        mapViewModel.buttonText = "start søk"
                    }
                ) {
                    Text("Ja")
                }
            }
        }
    )
}
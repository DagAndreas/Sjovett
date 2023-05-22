import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.CameraPositionState
import com.in2000_project.BoatApp.ui.screens.seaOrLandUrl
import com.in2000_project.BoatApp.data.MapState
import com.in2000_project.BoatApp.ui.components.CheckInternet
import com.in2000_project.BoatApp.viewmodel.MapViewModel
import com.in2000_project.BoatApp.viewmodel.SeaOrLandViewModel
import com.in2000_project.BoatApp.viewmodel.locationToLatLng
import com.plcoding.bottomnavwithbadges.ui.theme.Red
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun EndSearch(
    mapViewModel: MapViewModel,
    state: MapState,
    locationObtained: MutableState<Boolean>,
    modifier: Modifier,
    cameraPositionState: CameraPositionState,
    cameraZoom: Float,
    connection: CheckInternet
) {
    Button(
        onClick = {
            if (!connection.checkNetwork()) {
                Log.e("Internet connection", "Not connected!")
            } else {
                mapViewModel.updateLocation()
                val pos = locationToLatLng(state.lastKnownLocation)
                val seaOrLandViewModel =
                    SeaOrLandViewModel("$seaOrLandUrl?latitude=${pos.latitude}&longitude=${pos.longitude}&rapidapi-key=fc0719ee46mshf31ac457f36a8a9p15e288jsn324fc84023ff")

                mapViewModel.viewModelScope.launch {
                    var seaOrLandResponse = seaOrLandViewModel.getSeaOrLandResponse()
                    while (seaOrLandResponse == null) {
                        delay(100)
                        Log.i("MapScreen seaorland", "waiting for seaorlandresponse")
                        seaOrLandResponse = seaOrLandViewModel.getSeaOrLandResponse()
                    }

                    if (seaOrLandResponse.water) {
                        mapViewModel.oceanViewModel.setPath(pos)
                        mapViewModel.oceanViewModel.getOceanForecastResponse()

                        Log.i(
                            "sender den",
                            "${mapViewModel.oceanViewModel.oceanForecastResponseObject}"
                        )

                        if (!mapViewModel.mapUpdateThread.isRunning) {
                            mapViewModel.startButton(state.lastKnownLocation, pos)
                            mapViewModel.buttonText = "Avslutt søk"
                        } else {
                            mapViewModel.showDialog = true
                        }

                    } else if (seaOrLandResponse.water == false) {
                        mapViewModel.manIsOverboardInfoPopup = true
                        mapViewModel.infoTextMannOverBord =
                            "Vi kan ikke ta inn bølgedata når du er på land."
                    } else {
                        mapViewModel.manIsOverboardInfoPopup = true
                        mapViewModel.infoTextMannOverBord =
                            "Vi fikk ikke hentet dataene. Prøv igjen!"
                    }
                }
            }
            Log.i("MapScreen button", "Hei fra buttonpress")

        },
        modifier = modifier,
        shape = CircleShape,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Red),
        enabled = mapViewModel.enabled.value,
    ) {
        Text(
            text = mapViewModel.buttonText,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )

        LaunchedEffect(locationObtained.value) {
            delay(2000)
            if (locationObtained.value) {
                Log.i("MapScreen", "Zoomer inn på brukeren")
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(locationToLatLng(state.lastKnownLocation), cameraZoom),1500)
            }
        }
    }
}
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.in2000_project.BoatApp.R
import com.plcoding.bottomnavwithbadges.ui.theme.Black
import com.plcoding.bottomnavwithbadges.ui.theme.LightGrey
import com.plcoding.bottomnavwithbadges.ui.theme.White


@Composable
fun Livredning(
    openDrawer: () -> Unit
) {

    val imageList = listOf(
        R.drawable.livredning_voksne,
        R.drawable.livredning_barn
    )

    ZoomableBox(
        modifier = Modifier
            .background(color = LightGrey)
    ) {

        LazyColumn(
            modifier = Modifier
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX,
                    translationY = offsetY
                ),
        ) {
            items (imageList) {image ->

                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 15.dp, end = 15.dp, top = 15.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = White
                    )
                ) {
                    Image(
                        painter = painterResource(id = image),
                        contentDescription = "Livredning",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .padding(10.dp)
        ) {
            NavigationMenuButton(
                buttonIcon = Icons.Filled.Menu,
                onButtonClicked = { openDrawer() },
                modifier = Modifier
                    .background(
                        color = White,
                        shape = CircleShape
                    )
                    .border(
                        border = BorderStroke(1.dp, Black),
                        shape = CircleShape
                    )
                    .padding(10.dp)
                    .size(LocalConfiguration.current.screenWidthDp.dp * 0.07f)
            )
        }
    }
}

/*
@Composable
fun Livredning(
    openDrawer: () -> Unit
) {

    val imageList = listOf(
        R.drawable.livredning_voksne,
        R.drawable.livredning_barn
    )

    val scale = remember { mutableStateOf(1f) }
    val rotationState = remember { mutableStateOf(1f) }

    Box(
        modifier = Modifier
            .background(color = LightGrey)
            .pointerInput(Unit) {
                detectTransformGestures(panZoomLock = false) { centroid, pan, zoom, rotation ->
                    scale.value *= zoom
                }
            }
    ) {

        LazyColumn(
            modifier = Modifier
                .graphicsLayer(
                    // adding some zoom limits (min 50%, max 200%)
                    scaleX = maxOf(1f, minOf(3f, scale.value)),
                    scaleY = maxOf(1f, minOf(3f, scale.value))
                )
        ) {
            items (imageList) {image ->

                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 15.dp, end = 15.dp, top = 15.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = White
                    )
                ) {
                    Image(
                        painter = painterResource(id = image),
                        contentDescription = "Livredning",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp)
        ) {
            NavigationMenuButton(
                buttonIcon = Icons.Filled.Menu,
                onButtonClicked = { openDrawer() },
                modifier = Modifier
                    .background(
                        color = White,
                        shape = CircleShape
                    )
                    .border(
                        border = BorderStroke(1.dp, Black),
                        shape = CircleShape
                    )
                    .padding(10.dp)
                    .size(LocalConfiguration.current.screenWidthDp.dp * 0.07f)
            )
        }
    }
}
 */
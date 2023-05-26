import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.in2000_project.BoatApp.R
import com.in2000_project.BoatApp.view.components.navigation.MenuButton
import com.in2000_project.BoatApp.view.components.zoom.ZoomableBox
import com.plcoding.bottomnavwithbadges.ui.theme.Black
import com.plcoding.bottomnavwithbadges.ui.theme.LightGrey
import com.plcoding.bottomnavwithbadges.ui.theme.White

/** Represents the function that shows images of signals and marks at sea and how to handle them */
@Composable
fun Sjoemerkesystemet(
    openMenu: () -> Unit
) {

    val imageList = listOf(
        R.drawable.sjomerkesystemet_plakat1, R.drawable.sjomerkesystemet_plakat2
    )

    ZoomableBox(
        modifier = Modifier
            .background(color = LightGrey)
            .fillMaxSize()
    ) {

        LazyColumn(
            modifier = Modifier.graphicsLayer(
                scaleX = scale, scaleY = scale, translationX = offsetX, translationY = offsetY
            )
        ) {
            items(imageList) { image ->

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
                        contentDescription = "Sjømerkesystemet",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        Box(
            modifier = Modifier.padding(10.dp)
        ) {
            MenuButton(
                buttonIcon = Icons.Filled.Menu,
                onButtonClicked = { openMenu() },
                modifier = Modifier
                    .background(
                        color = White, shape = CircleShape
                    )
                    .border(
                        border = BorderStroke(1.dp, Black), shape = CircleShape
                    )
                    .padding(10.dp)
                    .size(LocalConfiguration.current.screenWidthDp.dp * 0.07f)
            )
        }
    }
}
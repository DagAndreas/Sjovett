import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
fun NavigationMenuButton(
    title: String = "",
    buttonIcon: ImageVector,
    onButtonClicked: () -> Unit) {

    IconButton(
        onClick = { onButtonClicked() } ,
        /*
        modifier = Modifier
            .size(LocalConfiguration.current.screenWidthDp.dp * 0.16f)
            .background(
                color = Color.Unspecified,
                shape = CircleShape
            )

         */

    ) {
        Icon(
            imageVector = buttonIcon,
            contentDescription = "",
            modifier = Modifier
                .background(
                    color = Color.White,
                    shape = CircleShape
                )
                .padding(10.dp)
                .size(LocalConfiguration.current.screenWidthDp.dp * 0.07f)
        )
    }
}
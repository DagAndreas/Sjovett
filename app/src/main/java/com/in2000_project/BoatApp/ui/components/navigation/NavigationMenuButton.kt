import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.plcoding.bottomnavwithbadges.ui.theme.White

@Composable
fun NavigationMenuButton(
    buttonIcon: ImageVector,
    onButtonClicked: () -> Unit,
    modifier: Modifier
) {

    IconButton(
        onClick = { onButtonClicked() } ,

    ) {
        Icon(
            imageVector = buttonIcon,
            contentDescription = "",
            modifier = modifier
        )
    }
}
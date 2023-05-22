package com.in2000_project.BoatApp.ui.components.navigation
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

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
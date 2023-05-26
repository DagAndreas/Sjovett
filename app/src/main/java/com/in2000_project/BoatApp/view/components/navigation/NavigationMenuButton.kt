package com.in2000_project.BoatApp.view.components.navigation

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

/** Represents the HamburgerMenu's Button */
@Composable
fun MenuButton(
    buttonIcon: ImageVector, onButtonClicked: () -> Unit, modifier: Modifier
) {
    IconButton(
        onClick = { onButtonClicked() },
    ) {
        Icon(
            imageVector = buttonIcon, contentDescription = "", modifier = modifier
        )
    }
}
package com.in2000_project.BoatApp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.core.graphics.toColorInt
import coil.compose.AsyncImage
import com.in2000_project.BoatApp.R


//@Preview
@Composable
fun VerktoyCard(name: String, color: String, icon: Painter) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    Box(
        modifier = Modifier
            .height(0.35 * screenHeight)
            .padding(start = 0.1 * screenWidth, end = 0.1 * screenWidth, bottom = 0.1 * screenWidth)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxHeight(0.9f)
                .fillMaxWidth()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
            Icon(
                painter = icon,
                contentDescription = "verktoyIcon",
                tint = Color.Unspecified,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(0.2 * screenWidth)
            )

        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .wrapContentSize(Alignment.Center)
                .fillMaxHeight(0.2f)
                .fillMaxWidth(0.5f)
                .background(
                    color = Color(color.toColorInt()),
                    shape = RoundedCornerShape(10.dp)
                )
                .shadow(
                    elevation = 0.dp,
                    shape = RoundedCornerShape(0.dp)
                )
        ) {
            Text(
                text = name,
                modifier = Modifier
                    .wrapContentSize(Alignment.Center)
                    .align(Alignment.Center),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

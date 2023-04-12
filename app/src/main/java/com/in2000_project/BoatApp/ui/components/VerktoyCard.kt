package com.in2000_project.BoatApp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.core.graphics.toColorInt
import coil.compose.AsyncImage


//@Preview
@Composable
fun VerktoyCard(name: String, color: String, icon: String) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 0.1 * screenWidth, end = 0.1 * screenWidth, top = 0.1 * screenWidth)
            .shadow(
                elevation = 20.dp
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = name,
                modifier = Modifier
                    .background(Color(color.toColorInt()))
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .wrapContentHeight(Alignment.CenterVertically)
                    .height(30.dp)
                    .padding(top = 2.dp),
                fontWeight = FontWeight.Bold
            )

            AsyncImage(
                model = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR_1yLt7BIszwuDrY55U_9gSL96cGqrnZdXJ5NQYv7vRWPJ0DguzA&s",
                contentDescription = "cardIcon",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
                    .fillMaxSize(0.5f)
            )

        }
    }
}



/*
@Composable
fun VerktoyCard(name: String, color: String, icon: String) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    Box(
        modifier = Modifier
            .wrapContentWidth(Alignment.CenterHorizontally)
            .border(BorderStroke(1.dp, Color.Black))
            .padding(20.dp)
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 0.1 * screenWidth,
                    end = 0.1 * screenWidth,
                    top = 0.1 * screenWidth
                )
                .shadow(
                    elevation = 20.dp
                )
        ) {
            AsyncImage(
                model = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR_1yLt7BIszwuDrY55U_9gSL96cGqrnZdXJ5NQYv7vRWPJ0DguzA&s",
                contentDescription = "cardIcon",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
                    .fillMaxSize(0.5f)
            )
        }

        ElevatedCard(
            modifier = Modifier
                .offset(x = 0.dp, y = -20.dp)
        ) {
            Text(
                text = name,
                modifier = Modifier
                    .background(Color(color.toColorInt()))
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .height(30.dp)
                    .padding(5.dp)
                    .shadow(elevation = 20.dp),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

 */
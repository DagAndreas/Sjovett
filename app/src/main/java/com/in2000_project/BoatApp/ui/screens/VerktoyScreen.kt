package com.in2000_project.BoatApp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.in2000_project.BoatApp.ui.components.VerktoyCard
import java.lang.reflect.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerktoyScreen() {
    val elements: List<List<String>> = listOf(
        listOf("Båtregler", "#66CCFF", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR_1yLt7BIszwuDrY55U_9gSL96cGqrnZdXJ5NQYv7vRWPJ0DguzA&s"),
        listOf("Førstehjelp", "#FF6961", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR_1yLt7BIszwuDrY55U_9gSL96cGqrnZdXJ5NQYv7vRWPJ0DguzA&s"),
        listOf("Promillekalkulator", "#FDB813", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR_1yLt7BIszwuDrY55U_9gSL96cGqrnZdXJ5NQYv7vRWPJ0DguzA&s")
    )

    LazyColumn() {
        items (elements) {cardData ->
            VerktoyCard(name = cardData[0], color = cardData[1], icon = cardData[2])
        }
    }

}
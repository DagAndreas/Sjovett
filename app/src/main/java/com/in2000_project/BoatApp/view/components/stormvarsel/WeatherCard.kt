package com.in2000_project.BoatApp.view.components.stormvarsel

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.in2000_project.BoatApp.R
import com.plcoding.bottomnavwithbadges.ui.theme.*

/** Represents the weather-cards that are displayed in Stormvarsel */
@Composable
fun WeatherCard(
    time: String,
    temperature: Double,
    windSpeed: Double,
    windDirection: Double,
    gustSpeed: Double,
    rainAmount: Double,
    weatherIcon: String
) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthSp = configuration.screenWidthDp.sp
    val screenHeight = configuration.screenHeightDp.dp
    val icon: Int
    val iconDesc: String

    val cornerShape = 20
    val grayColor = LightGrey
    val tempColor = when {
        temperature >= 0 -> Red
        else -> DarkBlue
    }

    // icons
    when(weatherIcon){
        "clearsky_day" -> {icon = R.drawable.clearsky_day; iconDesc = "Clear sky"}
        "clearsky_night" -> {icon = R.drawable.clearsky_night; iconDesc = "Clear sky"}
        "clearsky_polartwilight" -> {icon = R.drawable.clearsky_polartwilight; iconDesc = "Clear sky"}
        "cloudy" -> {icon = R.drawable.cloudy;iconDesc = "Cloudy"}
        "fair_day" -> {icon = R.drawable.fair_day;iconDesc = "Fair"}
        "fair_night" -> {icon = R.drawable.fair_night;iconDesc = "Fair"}
        "fair_polartwilight" -> {icon = R.drawable.fair_polartwilight;iconDesc = "Fair"}
        "fog" -> {icon = R.drawable.fog ;iconDesc = "Fog"}
        "heavyrain" -> {icon = R.drawable.heavyrain ;iconDesc = "Heavy rain"}
        "heavyrainandthunder" -> {icon = R.drawable.heavyrainandthunder ;iconDesc = "Heavy rain and thunder"}
        "heavyrainshowers_day" -> {icon = R.drawable.heavyrainshowers_day ;iconDesc = "Heavy rain showers"}
        "heavyrainshowers_night" -> {icon = R.drawable.heavyrainshowers_night ;iconDesc = "Heavy rain showers"}
        "heavyrainshowers_polartwilight" -> {icon = R.drawable.heavyrainshowers_polartwilight ;iconDesc = "Heavy rain showers"}
        "heavyrainshowersandthunder_day" -> {icon = R.drawable.heavyrainshowersandthunder_day ;iconDesc = "Heavy rain showers and thunder"}
        "heavyrainshowersandthunder_night" -> {icon = R.drawable.heavyrainshowersandthunder_night ;iconDesc = "Heavy rain showers and thunder"}
        "heavyrainshowersandthunder_polartwilight" -> {icon = R.drawable.heavyrainshowersandthunder_polartwilight ;iconDesc = "Heavy rain showers and thunder"}
        "heavysleet" -> {icon = R.drawable.heavysleet ;iconDesc = "Heavy sleet"}
        "heavysleetandthunder" -> {icon = R.drawable.heavysleetandthunder ;iconDesc = "Heavy sleet and thunder"}
        "heavysleetshowers_day" -> {icon = R.drawable.heavysleetshowers_day ;iconDesc = "Heavy sleet showers"}
        "heavysleetshowers_night" -> {icon = R.drawable.heavysleetshowers_night ;iconDesc = "Heavy sleet showers"}
        "heavysleetshowers_polartwilight" -> {icon = R.drawable.heavysleetshowers_polartwilight ;iconDesc = "Heavy sleet showers"}
        "heavysleetshowersandthunder_day" -> {icon = R.drawable.heavysleetshowersandthunder_day ;iconDesc = "Heavy sleet showers and thunder"}
        "heavysleetshowersandthunder_night" -> {icon = R.drawable.heavysleetshowersandthunder_night ;iconDesc = "Heavy sleet showers and thunder"}
        "heavysleetshowersandthunder_polartwilight" -> {icon = R.drawable.heavysleetshowersandthunder_polartwilight ;iconDesc = "Heavy sleet showers and thunder"}
        "heavysnow" -> {icon = R.drawable.heavysnow ;iconDesc = "Heavy snow"}
        "heavysnowandthunder" -> {icon = R.drawable.heavysnowandthunder ;iconDesc = "Heavy snow and thunder"}
        "heavysnowshowers_day" -> {icon = R.drawable.heavysnowshowers_day ;iconDesc = "Heavy snow showers"}
        "heavysnowshowers_night" -> {icon = R.drawable.heavysnowshowers_night ;iconDesc = "Heavy snow showers"}
        "heavysnowshowers_polartwilight" -> {icon = R.drawable.heavysnowshowers_polartwilight ;iconDesc = "Heavy snow showers"}
        "heavysnowshowersandthunder_day" -> {icon = R.drawable.heavysnowshowersandthunder_day ;iconDesc = "Heavy snow showers and thunder"}
        "heavysnowshowersandthunder_night" -> {icon = R.drawable.heavysnowshowersandthunder_day ;iconDesc = "Heavy snow showers and thunder"}
        "heavysnowshowersandthunder_polartwilight" -> {icon = R.drawable.heavysnowshowersandthunder_day ;iconDesc = "Heavy snow showers and thunder"}
        "lightrain" -> {icon = R.drawable.lightrain ;iconDesc = "Light rain"}
        "lightrainandthunder" -> {icon = R.drawable.lightrainandthunder ;iconDesc = "Light rain and thunder"}
        "lightrainshowers_day" -> {icon = R.drawable.lightrainshowers_day ;iconDesc = "Light rain showers"}
        "lightrainshowers_night" -> {icon = R.drawable.lightrainshowers_night ;iconDesc = "Light rain showers"}
        "lightrainshowers_polartwilight" -> {icon = R.drawable.lightrainshowers_polartwilight ;iconDesc = "Light rain showers"}
        "lightrainshowersandthunder_day" -> {icon = R.drawable.lightrainshowersandthunder_day ;iconDesc = "Light rain showers and thunder"}
        "lightrainshowersandthunder_night" -> {icon = R.drawable.lightrainshowersandthunder_night ;iconDesc = "Light rain showers and thunder"}
        "lightrainshowersandthunder_polartwilight" -> {icon = R.drawable.lightrainshowersandthunder_polartwilight ;iconDesc = "Light rain showers and thunder"}
        "lightsleet" -> {icon = R.drawable.lightsleet ;iconDesc = "Light sleet"}
        "lightsleetandthunder" -> {icon = R.drawable.lightsleetandthunder ;iconDesc = "Light sleet and thunder"}
        "lightsleetshowers_day" -> {icon = R.drawable.lightsleetshowers_day ;iconDesc = "Light sleet showers"}
        "lightsleetshowers_night" -> {icon = R.drawable.lightsleetshowers_night ;iconDesc = "Light sleet showers"}
        "lightsleetshowers_polartwilight" -> {icon = R.drawable.lightsleetshowers_polartwilight ;iconDesc = "Light sleet showers"}
        "lightsnow" -> {icon = R.drawable.lightsnow ;iconDesc = "Light snow"}
        "lightsnowandthunder" -> {icon = R.drawable.lightsnowandthunder ;iconDesc = "Light snow and thunder"}
        "lightsnowshowers_day" -> {icon = R.drawable.lightsnowshowers_day ;iconDesc = "Light snow showers"}
        "lightsnowshowers_night" -> {icon = R.drawable.lightsnowshowers_night ;iconDesc = "Light snow showers"}
        "lightsnowshowers_polartwilight" -> {icon = R.drawable.lightsnowshowers_polartwilight ;iconDesc = "Light snow showers"}
        "lightssleetshowersandthunder_day" -> {icon = R.drawable.lightssleetshowersandthunder_day ;iconDesc = "Light sleet showers and thunder"}
        "lightssleetshowersandthunder_night" -> {icon = R.drawable.lightssleetshowersandthunder_night ;iconDesc = "Light sleet showers and thunder"}
        "lightssleetshowersandthunder_polartwilight" -> {icon = R.drawable.lightssleetshowersandthunder_polartwilight ;iconDesc = "Light sleet showers and thunder"}
        "lightssnowshowersandthunder_day" -> {icon = R.drawable.lightssnowshowersandthunder_day ;iconDesc = "Light snow showers and thunder"}
        "lightssnowshowersandthunder_night" -> {icon = R.drawable.lightssnowshowersandthunder_night ;iconDesc = "Light snow showers and thunder"}
        "lightssnowshowersandthunder_polartwilight" -> {icon = R.drawable.lightssnowshowersandthunder_polartwilight ;iconDesc = "Light snow showers and thunder"}
        "partlycloudy_day" -> {icon = R.drawable.partlycloudy_day ;iconDesc = "Partly cloudy"}
        "partlycloudy_night" -> {icon = R.drawable.partlycloudy_night ;iconDesc = "Partly cloudy"}
        "partlycloudy_polartwilight" -> {icon = R.drawable.partlycloudy_polartwilight ;iconDesc = "Partly cloudy"}
        "rain" -> {icon = R.drawable.rain ;iconDesc = "Rain"}
        "rainandthunder" -> {icon = R.drawable.rainandthunder ;iconDesc = "Rain and thunder"}
        "rainshowers_day" -> {icon = R.drawable.rainshowers_day ;iconDesc = "Rain showers"}
        "rainshowers_night" -> {icon = R.drawable.rainshowers_night ;iconDesc = "Rain showers"}
        "rainshowers_polartwilight" -> {icon = R.drawable.rainshowers_polartwilight ;iconDesc = "Rain showers"}
        "rainshowersandthunder_day" -> {icon = R.drawable.rainshowersandthunder_day ;iconDesc = "Rain showers and thunder"}
        "rainshowersandthunder_night" -> {icon = R.drawable.rainshowersandthunder_night ;iconDesc = "Rain showers and thunder"}
        "rainshowersandthunder_polartwilight" -> {icon = R.drawable.rainshowersandthunder_polartwilight ;iconDesc = "Rain showers and thunder"}
        "sleet" -> {icon = R.drawable.sleet ;iconDesc = "Sleet"}
        "sleetandthunder" -> {icon = R.drawable.sleetandthunder ;iconDesc = "Sleet and thunder"}
        "sleetshowers_day" -> {icon = R.drawable.sleetshowers_day ;iconDesc = "Sleet showers"}
        "sleetshowers_night" -> {icon = R.drawable.sleetshowers_night ;iconDesc = "Sleet showers"}
        "sleetshowers_polartwilight" -> {icon = R.drawable.sleetshowers_polartwilight ;iconDesc = "Sleet showers"}
        "sleetshowersandthunder_day" -> {icon = R.drawable.sleetshowersandthunder_day ;iconDesc = "Sleet showers and thunder"}
        "sleetshowersandthunder_night" -> {icon = R.drawable.sleetshowersandthunder_night ;iconDesc = "Sleet showers and thunder"}
        "sleetshowersandthunder_polartwilight" -> {icon = R.drawable.sleetshowersandthunder_polartwilight ;iconDesc = "Sleet showers and thunder"}
        "snow" -> {icon = R.drawable.snow ;iconDesc = "Snow"}
        "snowandthunder" -> {icon = R.drawable.snowandthunder ;iconDesc = "Snow and thunder"}
        "snowshowers_day" -> {icon = R.drawable.snowshowers_day ;iconDesc = "Snow showers"}
        "snowshowers_night" -> {icon = R.drawable.snowshowers_night ;iconDesc = "Snow showers"}
        "snowshowers_polartwilight" -> {icon = R.drawable.snowshowers_polartwilight ;iconDesc = "Snow showers"}
        "snowshowersandthunder_day" -> {icon = R.drawable.snowshowersandthunder_day ;iconDesc = "Snow showers and thunder"}
        "snowshowersandthunder_night" -> {icon = R.drawable.snowshowersandthunder_night ;iconDesc = "Snow showers and thunder"}
        "snowshowersandthunder_polartwilight" -> {icon = R.drawable.snowshowersandthunder_polartwilight ;iconDesc = "Snow showers and thunder"}
        else -> {icon = R.drawable.round_cloud_sync_24; iconDesc = "Searching for weather"; Log.e("Ikon", "Could not find drawable: $weatherIcon")}
    }

    Box(
        modifier = Modifier
            .height(0.35 * screenHeight)
            .width(0.8 * screenWidth)
            .padding(
                start = 0.05 * screenWidth, end = 0.05 * screenWidth, bottom = 0.05 * screenWidth
            )
            .background(
                color = White, shape = RoundedCornerShape(cornerShape.dp)
            )
    ) {
        Box( // Main box
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxHeight(0.9f)
                .fillMaxWidth()
                .background(
                    color = grayColor, // grayColor
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center)
            ) {
                Row( // Top row
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = iconDesc,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(0.15 * screenWidth)
                    )
                    Text(
                        text = "$temperature C°",
                        fontSize = (0.10 * screenWidthSp),
                        color = tempColor
                    )
                } // End of "Top row"

                Row { // Bottom row
                    Row( // Wind row
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_air_24),
                            contentDescription = "Wind icon",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(0.06 * screenWidth)
                        )
                        Text(
                            text = "$windSpeed", fontSize = (0.07 * screenWidthSp), color = Black
                        )
                        Column(
                            modifier = Modifier.align(Alignment.Top)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.baseline_arrow_right_alt_24),
                                contentDescription = "Wind arrow",
                                modifier = Modifier
                                    .wrapContentSize()
                                    .size(0.05 * screenWidth)
                                    .graphicsLayer(
                                        rotationZ = windDirection.toFloat()
                                    )
                            )
                            Text(
                                text = "($gustSpeed)",
                                fontSize = (0.03 * screenWidthSp),
                                color = Black
                            )
                        }

                    } // End of "Wind row"
                    Row( // Rain row
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.water_drop_24),
                            contentDescription = "Rain icon",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(0.07 * screenWidth)
                        )
                        Text(
                            text = "$rainAmount",
                            fontSize = (0.07 * screenWidthSp),
                            color = Black,
                            modifier = Modifier.align(Alignment.Bottom)
                        )
                        Text(
                            text = "mm",
                            fontSize = (0.03 * screenWidthSp),
                            color = Black,
                            modifier = Modifier.align(Alignment.Bottom)
                        )
                    } // End of "Rain row"
                } // End of "Bottom row"
            }
        } // End of "Main box"

        val hour = time.split(" ")[3].split(":")
        val useTime = "${hour[0]}:${hour[1]}"

        Box( // Header
            modifier = Modifier
                .align(Alignment.TopCenter)
                .wrapContentSize(Alignment.Center)
                .fillMaxHeight(0.2f)
                .fillMaxWidth(0.5f)
                .background(
                    color = grayColor, shape = RoundedCornerShape(10.dp)
                )
                .border(
                    border = BorderStroke(1.dp, Black), shape = RoundedCornerShape(10.dp)
                )
        ) {
            Text(
                text = useTime,
                modifier = Modifier
                    .wrapContentSize(Alignment.Center)
                    .align(Alignment.Center)
                    .background(grayColor),
                fontWeight = FontWeight.Bold
            )
        }// End of "Header"
    }
}
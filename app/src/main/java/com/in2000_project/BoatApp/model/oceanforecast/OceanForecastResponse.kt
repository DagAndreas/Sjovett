package com.in2000_project.BoatApp.model.oceanforecast

data class OceanForecastResponse(
    val geometry: Geometry,
    val properties: Properties,
    val type: String
)

fun getEmptyOceanForecastResponse(): OceanForecastResponse {
    return OceanForecastResponse(
        geometry = Geometry(listOf(), ""),
        properties = Properties(
            meta = Meta(
                units = Units("", "", "", "", ""),
                updated_at = ""
            ),
            timeseries = listOf()
        ),
        type = ""
    )
}
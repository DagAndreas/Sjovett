package com.in2000_project.BoatApp.model.oceanforecast

/** We use this data class in oceanForecast to display the proper informaton */
data class OceanForecastResponse(
    val geometry: Geometry,
    val properties: Properties,
    val type: String
)

/**  Returns an empty OceanForecastResponse. Helps us avoid IllegalJsonParameter  */
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
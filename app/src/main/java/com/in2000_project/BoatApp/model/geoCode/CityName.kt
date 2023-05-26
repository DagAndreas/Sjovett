package com.in2000_project.BoatApp.model.geoCode

data class CityName(
    val name: String,
    val country: String
){
    /** Opens up for different formats of input-string*/
    fun matchesSearch(search: String): Boolean{ //de forskjellige gyldige søkene
        val combinations = listOf(
            name,
            "$name, $country",
            "$name,$country",
            "$name $country",
            "$country, $name",
        )

        return combinations.any{
            it.startsWith(search, ignoreCase = true)
        }
    }
}
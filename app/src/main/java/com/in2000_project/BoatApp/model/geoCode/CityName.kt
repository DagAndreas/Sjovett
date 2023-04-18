package com.in2000_project.BoatApp.model.geoCode

data class CityName(
    val name: String,
    val country: String
    /*val latitude: Double,
    val longitude: Double,
    val country: String*/
){
    fun matchesSearch(search: String): Boolean{ //de forskjellige gyldige søkene
        val combinations = listOf(
            "$name",
            "$name, $country",
            "$name,$country",
            "$name $country",
            "$country, $name",
        )

        return combinations.any{
            it.contains(search, ignoreCase = true) //gir treff uansett om stor eller liten bokstav
        }
    }
}
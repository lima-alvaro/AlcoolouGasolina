package com.example.exemplosimplesdecompose.data

data class FuelStation(
    val id: String,
    val name: String,
    val alcoholPrice: Double,
    val gasolinePrice: Double,
    val date: Long,
    val latitude: Double,
    val longitude: Double
) {
    fun hasLocation(): Boolean = latitude != 0.0 || longitude != 0.0
}

package com.example.kongsikeretariders.data

data class Rides(
    val rideId: String = "",
    val date: Long = 0,
    val time: String = "",
    val origin: String = "",
    val destination: String = "",
    val fare: Float = 0f,
    val userId: String = "",
    val joined: List<String> = listOf(),
    val cancelled: List<String> = listOf()
)

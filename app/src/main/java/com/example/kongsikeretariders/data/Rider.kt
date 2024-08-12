package com.example.kongsikeretariders.data

data class Rider(
    val ic: String = "",
    val password: String = "",
    val gender: Boolean = false,
    val phoneNumber: String = "",
    val email: String = "",
    val address: String = "",
)

data class RiderInfo(
    val ic: String = "",
    val password: String = "",
    val profilePicUrl: String = "",
    val userDataUrl: String = "",
)
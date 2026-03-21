package com.example.dinemaster.model

data class LoginResponse(
    val settings: Settings,
    val data: UserData
)

data class UserData(
    val id: String,
    val token: String,
    val name: String,
    val email: String,
    val restaurant_id: String

)
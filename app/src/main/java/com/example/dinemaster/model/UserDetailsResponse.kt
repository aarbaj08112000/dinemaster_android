package com.example.dinemaster.model

data class UserDetailsResponse(
    val settings: Settings,
    val data: UserDetailsData
)


data class UserDetailsData(
    val user_id: String,
    val restaurant_id: String,
    val user_role: String,
    val user_email: String,
    val phone: String,
    val user_name: String,
    val last_login_at: String,
    val status: String,
    val api_token: String
)
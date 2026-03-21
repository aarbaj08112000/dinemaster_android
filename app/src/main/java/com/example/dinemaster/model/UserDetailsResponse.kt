package com.example.dinemaster.model

data class UserDetailsResponse(
    val settings: Settings,
    val data: UserDetailsData
)


//data class UserDetailsData(
//    val user_id: String,
//    val restaurant_id: String,
//    val user_role: String,
//    val user_email: String,
//    val phone: String,
//    val user_name: String,
//    val last_login_at: String,
//    val status: String,
//    val api_token: String
//)

data class UserDetailsData(
    val user_id: String,
    val restaurant_id: String,
    val user_role: String,
    val role_name: String,
    val user_email: String,
    val phone: String,
    val user_name: String,
    val status: String,
    val total_orders: Int,
    val today_orders: Int,
    val last_7_days_orders: Int,
    val last_login_at: String? = null,
    val api_token: String? = null
)
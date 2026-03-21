package com.example.dinemaster.model

data class RestaurantResponse(
    val settings: Settings,
    val data: RestaurantData
)

data class RestaurantData(
    val restaurant_id: String,
    val name: String,
    val legal_name: String,
    val gstin: String,
    val contact_email: String,
    val contact_phone: String,
    val address_line1: String,
    val address_line2: String,
    val city: String,
    val state: String,
    val postal_code: String,
    val country: String,
    val logo_url: String
)
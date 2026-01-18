package com.example.dinemaster.model

data class MenuItemApi(
    val item_id: String,
    val restaurant_id: String,
    val category_id: String,
    val name: String,
    val code: String,
    val description: String,
    val base_price: String,
    val veg_type: String,          // VEG / NON_VEG
    val image_url: String?,
    val is_available: String,
    val is_active: String,
    val tax_rate: String,
    val tax_breakdown: String,
    val added_by: String,
    val updated_by: String?,
    val added_date: String,
    val updated_date: String,
    var qty: Int = 0
)



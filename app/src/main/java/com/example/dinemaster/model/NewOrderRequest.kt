package com.example.dinemaster.model

data class NewOrderRequest(
    val restaurant_id: String,
    val table_no: String,
    val items: List<Item>
)

data class Item(
    val item_id: String,
    val item_name: String,
    val unit_price: String,
    val quantity: String,
    val addons: List<Addon>? = null
)

data class Addon(
    val addon_option_id: String,
    val addon_name: String,
    val price_delta: String
)
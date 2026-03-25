package com.example.dinemaster.model

data class UpdateOrderRequest(
    val order_id: Int,
    val items: List<UpdateItem>
)

data class UpdateItem(
    val order_item_id: Int? = null,
    val menu_id: Int? = null,
    val quantity: Int,
    val price: Double? = null
)
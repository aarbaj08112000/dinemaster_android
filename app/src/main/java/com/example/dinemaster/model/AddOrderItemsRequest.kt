package com.example.dinemaster.model

data class AddOrderItemsRequest(
    val order_id: Int,
    val items: List<AddItem>
)

data class AddItem(
    val menu_id: Int,
    val quantity: Int,
    val price: Double
)
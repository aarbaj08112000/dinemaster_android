package com.example.dinemaster.model

data class OrderUpdateResponse(
    val settings: Settings,
    val data: UpdateOrderData
)

data class UpdateOrderData(
    val order_id: Int
)
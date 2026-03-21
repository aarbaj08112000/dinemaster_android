package com.example.dinemaster.model

data class NewOrderResponse(
    val settings: Settings,
    val data: NewOrderData
)
data class NewOrderData(
    val order_id: Int
)
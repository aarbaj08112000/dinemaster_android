package com.example.dinemaster.model

data class OrdersResponse(
    val settings: Settings,
    val data: List<OrderData>
)

data class OrderData(
    val order_id: String,
    val table_id: String,
    val order_type: String,
    val status: String,
    val subtotal_amount: String,
    val total_payable: String,
    val payment_status: String,
    val placed_at: String,
    val items: List<OrderItem>
)

data class OrderItem(
    val id: String,
    val item_name: String,
    val unit_price: String,
    val quantity: String
)
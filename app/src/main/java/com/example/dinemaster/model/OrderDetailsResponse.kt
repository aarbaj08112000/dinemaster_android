package com.example.dinemaster.model

data class OrderDetailsResponse(
    val settings: Settings,
    val data: OrderDetailData
)

data class OrderDetailData(
    val order_id: String,
    val table_id: String,
    val order_type: String,
    val status: String,
    val placed_at: String,
    val total_payable: String,
    val payment_status: String,
    val items: List<OrderItemData>
)

data class OrderItemData(
    val order_item_id: String,
    val item_name: String,
    val veg_type: String,
    val unit_price: String,
    val quantity: String
)
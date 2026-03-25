package com.example.dinemaster.model

data class OrderDetailsResponse(
    val settings: Settings,
    val data: OrderDetailData
)

data class OrderDetailData(
    val order_id: String,
    val order_number: String?,
    val restaurant_id: String,
    val table_id: String,
    val customer_id: String?,
    val waiter_id: String?,
    val order_type: String,
    val status: String,
    val placed_at: String,
    val completed_at: String?,
    val subtotal_amount: String,
    val discount_amount: String,
    val service_charge_pct: String,
    val service_charge_amt: String,
    val tax_amount: String,
    val rounding_adjustment: String,
    val total_payable: String,
    val tax_breakdown: Any?, // can be null or object
    val notes: String?,
    val payment_status: String,
    val is_active: String,
    val added_by: String,
    val updated_by: String?,
    val added_date: String,
    val updated_date: String,
    val gst_applicable: String,
    val gst_percentage: String,
    val restaurant_name: String,
    val items: List<OrderItemData>
)

data class OrderItemData(
    val order_item_id: String,
    val order_id: String,
    val item_id: String,
    val item_name: String,
    val veg_type: String,
    val unit_price: String,
    val quantity: String, // ⚠️ changed from Int → String ("1.00")
    val notes: String?,
    val status: String,
    val tax_rate: String,
    val tax_breakdown: Any?,
    val tax_amount: String,
    val line_subtotal: String,
    val line_discount: String,
    val line_total: String,
    val added_by: String,
    val updated_by: String?,
    val added_date: String,
    val updated_date: String,
    val addons: List<Any> // currently empty array
)
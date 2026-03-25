package com.example.dinemaster.model

data class DeleteItemRequest(
    val order_id: String,
    val item_id: String
)
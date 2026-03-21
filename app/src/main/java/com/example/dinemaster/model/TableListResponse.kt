package com.example.dinemaster.model

data class TableListResponse(
    val settings: Settings,
    val data: List<TableData>
)

data class TableData(
    val table_id: String,
    val table_code: String,
    val table_name: String,
    val capacity: String,
    val status: String,
    val restaurant_id: String
)
package com.example.dinemaster.model

data class CategoryResponse(
    val data: List<Category>
)

data class Category(
    val category_id: String,
    val restaurant_id: String,
    val name: String,
    val description: String,
    val sort_order: String,
    val is_active: String,
    val added_by: String,
    val updated_by: String?,
    val added_date: String,
    val updated_date: String
)


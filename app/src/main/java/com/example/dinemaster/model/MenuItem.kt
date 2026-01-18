package com.example.dinemaster.model

import java.io.Serializable

data class MenuItem(
    val imageRes: Int,
    val name: String,
    val category: String,
    val price: Double,
    var qty: Int = 0
) : Serializable

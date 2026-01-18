package com.example.dinemaster

import com.example.dinemaster.model.CategoryRequest
import com.example.dinemaster.model.CategoryResponse
import com.example.dinemaster.model.LoginRequest
import com.example.dinemaster.model.LoginResponse
import com.example.dinemaster.model.MenuItemResponse
import com.example.dinemaster.model.MenuRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("categories/index/id/{id}")
    suspend fun getCategories(
        @Path("id") id: Int
    ): CategoryResponse

    @POST("menu_items/get_menu")
    suspend fun getMenuItems(
        @Body request: MenuRequest
    ): MenuItemResponse

}

package com.example.dinemaster.helper

import com.example.dinemaster.model.CategoryRequest
import com.example.dinemaster.model.CategoryResponse
import com.example.dinemaster.model.ForgotPasswordRequest
import com.example.dinemaster.model.ForgotPasswordResponse
import com.example.dinemaster.model.ItemDetailsRequest
import com.example.dinemaster.model.ItemDetailsResponse
import com.example.dinemaster.model.LoginRequest
import com.example.dinemaster.model.LoginResponse
import com.example.dinemaster.model.MenuItemResponse
import com.example.dinemaster.model.MenuRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("categories")
    suspend fun getCategories(
        @Query("id") id: Int
    ): CategoryResponse

    @POST("menu_items/get_menu")
    suspend fun getMenuItems(
        @Body request: MenuRequest
    ): MenuItemResponse

    @POST("user_login/forgot_password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): Response<ForgotPasswordResponse>

    @POST("menu_items/get_item_details")
    suspend fun getItemDetails(
        @Body request: ItemDetailsRequest
    ): ItemDetailsResponse
}

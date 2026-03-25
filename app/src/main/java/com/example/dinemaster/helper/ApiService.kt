package com.example.dinemaster.helper

import com.example.dinemaster.model.AddOrderItemsRequest
import com.example.dinemaster.model.CategoryRequest
import com.example.dinemaster.model.CategoryResponse
import com.example.dinemaster.model.DeleteItemRequest
import com.example.dinemaster.model.ForgotPasswordRequest
import com.example.dinemaster.model.ForgotPasswordResponse
import com.example.dinemaster.model.ItemDetailsRequest
import com.example.dinemaster.model.ItemDetailsResponse
import com.example.dinemaster.model.LoginRequest
import com.example.dinemaster.model.LoginResponse
import com.example.dinemaster.model.MenuItemResponse
import com.example.dinemaster.model.MenuRequest
import com.example.dinemaster.model.NewOrderRequest
import com.example.dinemaster.model.NewOrderResponse
import com.example.dinemaster.model.OrderDetailsResponse
import com.example.dinemaster.model.OrderUpdateResponse
import com.example.dinemaster.model.OrdersResponse
import com.example.dinemaster.model.RestaurantResponse
import com.example.dinemaster.model.TableListResponse
import com.example.dinemaster.model.UpdateOrderRequest
import com.example.dinemaster.model.UserDetailsResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
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

    @POST("user_login/get_restaurant_details")
    suspend fun getRestaurantDetails(
//        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    ): RestaurantResponse

    @POST("user_login/get_user_details")
    suspend fun getUserDetails(
        @Body params: Map<String, String>
    ): UserDetailsResponse

    @POST("gets_orders")
    suspend fun getOrders(
        @Body params: Map<String, String>
    ): OrdersResponse

    @POST("get_order_details")
    suspend fun getOrderDetails(
        @Body body: Map<String, String>
    ): OrderDetailsResponse

    @POST("new_orders")
    suspend fun createOrder(
        @Body request: NewOrderRequest
    ): Response<NewOrderResponse>

    @POST("tables")
    suspend fun getTables(
        @Body params: Map<String, String>
    ): TableListResponse

    @POST("update_item")
    suspend fun updateOrderItems(
        @Body request: UpdateOrderRequest
    ): OrderUpdateResponse

    @POST("add_order_items")
    suspend fun addOrderItems(
        @Body request: AddOrderItemsRequest
    ): OrderUpdateResponse

    @POST("delete_item")
    suspend fun deleteOrderItem(
        @Body request: DeleteItemRequest
    ): OrderUpdateResponse

}

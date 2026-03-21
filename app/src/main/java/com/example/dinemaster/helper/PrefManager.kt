package com.example.dinemaster.helper

import android.content.Context
import android.content.SharedPreferences
import com.example.dinemaster.model.UserData
import com.google.gson.Gson

object PrefManager {

    private const val PREF_NAME = "MyAppPrefs"

    private const val KEY_IS_LOGGED_IN = "isLoggedIn"
    private const val KEY_AUTH_TOKEN = "authToken"
    private const val KEY_USER_NAME = "userName"
    private const val KEY_USER_ID = "userId"
    private const val KEY_USER_EMAIL = "userEmail"
    private const val KEY_USER_MOBILE = "userMobile"
    private const val KEY_USER_DETAILS = "userDetails"
    private const val KEY_RESTAURANT_ID = "restaurant_id"
    private const val KEY_SUBSCRIPTION_EXPIRED = "subscription_expired"
    private const val KEY_PRINT_COUNT = "print_count"

    private lateinit var prefs: SharedPreferences

    // 🔹 Initialize in Application class
    fun init(context: Context) {
        if (!::prefs.isInitialized) {
            prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }
    }

    // 🔹 Save full login data
    fun saveLoginData(userData: UserData) {

        val gson = Gson()

        prefs.edit().apply {

            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_AUTH_TOKEN, userData.token)
            putString(KEY_USER_NAME, userData.name)
            putString(KEY_USER_EMAIL, userData.email)
            putString(KEY_USER_ID, userData.id)
            putString(KEY_RESTAURANT_ID, userData.restaurant_id)

            // Save full user JSON
            putString(KEY_USER_DETAILS, gson.toJson(userData))

            apply()
        }
    }

    // 🔹 Save token separately
    fun saveToken(token: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun getToken(): String {
        return prefs.getString(KEY_AUTH_TOKEN, "") ?: ""
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getUserName(): String {
        return prefs.getString(KEY_USER_NAME, "") ?: ""
    }

    fun getUserEmail(): String {
        return prefs.getString(KEY_USER_EMAIL, "") ?: ""
    }

    fun getUserMobile(): String {
        return prefs.getString(KEY_USER_MOBILE, "") ?: ""
    }

    fun getUserId(): String {
        return prefs.getString(KEY_USER_ID, "") ?: ""
    }

    fun getRestaurantId(): String {
        return prefs.getString(KEY_RESTAURANT_ID, "") ?: ""
    }
    fun getUserDetails(): UserData? {

        val json = prefs.getString(KEY_USER_DETAILS, null) ?: return null

        return try {
            Gson().fromJson(json, UserData::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun clear() {

        prefs.edit().apply {

            putBoolean(KEY_IS_LOGGED_IN, false)
            putString(KEY_AUTH_TOKEN, "")

            remove(KEY_USER_NAME)
            remove(KEY_USER_EMAIL)
            remove(KEY_USER_MOBILE)
            remove(KEY_USER_ID)
            remove(KEY_USER_DETAILS)
            remove(KEY_RESTAURANT_ID)

            apply()
        }
    }

    fun setSubscriptionExpired(isExpired: Boolean) {
        prefs.edit().putBoolean(KEY_SUBSCRIPTION_EXPIRED, isExpired).apply()
    }

    fun isSubscriptionExpired(): Boolean {
        return prefs.getBoolean(KEY_SUBSCRIPTION_EXPIRED, false)
    }

    fun setPrintCount(count: Int) {
        prefs.edit().putInt(KEY_PRINT_COUNT, count).apply()
    }

    fun getPrintCount(): Int {
        return prefs.getInt(KEY_PRINT_COUNT, 1)
    }
}
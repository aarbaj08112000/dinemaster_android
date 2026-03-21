package com.example.dinemaster.helper

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.example.dinemaster.LoginActivity
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject
class AuthInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val token = PrefManager.getToken()

        val requestBuilder = chain.request().newBuilder()

        if (token.isNotEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        val request = requestBuilder.build()

        val response = chain.proceed(request)

        // 🔴 Case 1: HTTP 401
        if (response.code == 401) {
            triggerLogout()
            return response
        }

        val responseBody = response.body ?: return response
        val contentType = responseBody.contentType()

        if (contentType?.subtype == "json") {

            val responseString = responseBody.string()

            try {

                val json = JSONObject(responseString)
                val settings = json.optJSONObject("settings")
                val success = settings?.optInt("success")

                // 🔴 Case 2: Token expired inside API response
                if (success == -1) {
                    triggerLogout()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

            // recreate response body
            return response.newBuilder()
                .body(responseString.toResponseBody(contentType))
                .build()
        }

        return response
    }

    private fun triggerLogout() {
        Handler(Looper.getMainLooper()).post {
            forceLogout(context)
        }
    }
}
fun forceLogout(context: Context) {
    PrefManager.clear()

    val intent = Intent(context, LoginActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    context.startActivity(intent)
}



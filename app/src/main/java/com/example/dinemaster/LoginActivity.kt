package com.example.dinemaster
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Patterns
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.example.dinemaster.databinding.ActivityLoginBinding
import com.example.dinemaster.model.LoginRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            when {
                email.isEmpty() || password.isEmpty() -> {
                    showSnackbar("Email and password cannot be empty", isError = true)
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    showSnackbar("Please enter a valid email address", isError = true)
                }
                else -> {
                    // ✅ Call API here
                    callLoginApi(email, password)
                }
            }
        }

        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
        }
    }

    private fun callLoginApi(email: String, password: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.login(LoginRequest(email, password))

                if (response.settings.success) {
                    // ✅ Save login state & token
                    val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    sharedPref.edit()
                        .putBoolean("isLoggedIn", true)
                        .putString("authToken", response.data.token)
                        .putString("userName", response.data.name)
                        .putString("userEmail", response.data.email)
                        .apply()

                    showSnackbar(response.settings.message, isError = false)

                    // Delay for user feedback
                    delay(1500)
                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                    finish()

                } else {
                    showSnackbar(response.settings.message, isError = true)
                }

            } catch (e: Exception) {
                showSnackbar("Error: ${e.message}", isError = true)
            }
        }
    }


}


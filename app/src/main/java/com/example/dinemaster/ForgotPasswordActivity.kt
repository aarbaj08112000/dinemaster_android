package com.example.dinemaster

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.dinemaster.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toggle eye for password
        binding.ivToggleNewPass.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            togglePasswordVisibility(binding.etNewPassword, binding.ivToggleNewPass, isPasswordVisible)
        }

        // Toggle eye for confirm password
        binding.ivToggleConfirmPass.setOnClickListener {
            isConfirmPasswordVisible = !isConfirmPasswordVisible
            togglePasswordVisibility(binding.etConfirmPassword, binding.ivToggleConfirmPass, isConfirmPasswordVisible)
        }

        binding.ivBack.setOnClickListener {
            startActivity(Intent(this@ForgotPasswordActivity, LoginActivity::class.java))
        }

        binding.btnResetPassword.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etNewPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            when {
                email.isEmpty() -> {
                    showSnackbar("Email cannot be empty", isError = true, anchorView = binding.btnResetPassword)
                    binding.etEmail.requestFocus()
                    return@setOnClickListener
                }

                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    showSnackbar("Enter a valid email address", isError = true, anchorView = binding.btnResetPassword)
                    binding.etEmail.requestFocus()
                    return@setOnClickListener
                }
                password.isEmpty() || confirmPassword.isEmpty() -> {
                    showSnackbar("Password fields cannot be empty", isError = true)
                    return@setOnClickListener
                }

                password.length < 6 -> {
                    showSnackbar("Password must be at least 6 characters", isError = true)
                    return@setOnClickListener
                }

                password != confirmPassword -> {
                    showSnackbar("Passwords do not match", isError = true)
                    return@setOnClickListener
                }

                else -> {
                    showSnackbar("Password reset successful", isError = false)
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun togglePasswordVisibility(editText: EditText, toggleView: ImageView, isVisible: Boolean) {
        if (isVisible) {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            toggleView.setImageResource(R.drawable.ic_eye) // 👁️ open eye icon
        } else {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            toggleView.setImageResource(R.drawable.ic_eye_off) // 👁️ closed eye icon
        }
        // Move cursor to the end after toggling
        editText.setSelection(editText.text.length)
    }
}



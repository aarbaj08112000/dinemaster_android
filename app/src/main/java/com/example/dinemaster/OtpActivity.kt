package com.example.dinemaster

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.dinemaster.databinding.ActivityOtpBinding

class OtpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnVerifyOtp.setOnClickListener {
            val otp = binding.etOtp.text.toString().trim()

            when {
                otp.isEmpty() -> {
                    showSnackbar("OTP cannot be empty", isError = true)
                    return@setOnClickListener
                }

                otp.length != 6 -> {
                    showSnackbar("Enter a valid 6-digit OTP", isError = true)
                    return@setOnClickListener
                }

                else -> {
                    showSnackbar("OTP Verified Successfully", isError = false)
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish() // close OTP screen
                }
            }
        }
    }
}

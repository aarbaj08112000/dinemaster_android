package com.example.dinemaster.model

data class ForgotPasswordRequest(
    val email: String,
    val password: String
)
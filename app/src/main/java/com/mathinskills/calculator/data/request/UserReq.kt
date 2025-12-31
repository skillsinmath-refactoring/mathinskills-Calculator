package com.mathinskills.calculator.data.request

data class SignupRequest(
    val username: String,
    val email: String?,
    val password: String
)

data class LoginRequest(
    val username: String,
    val password: String
)

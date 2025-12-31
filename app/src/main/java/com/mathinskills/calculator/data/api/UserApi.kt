package com.mathinskills.calculator.data.api

import com.mathinskills.calculator.data.request.LoginRequest
import com.mathinskills.calculator.data.request.SignupRequest
import com.mathinskills.calculator.data.response.LoginResponse
import com.mathinskills.calculator.data.response.SignupResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi {

    @POST("/api/auth/signup/")
    suspend fun signup(@Body request: SignupRequest): SignupResponse

    @POST("/api/auth/login/")
    suspend fun login(@Body request: LoginRequest): LoginResponse
}
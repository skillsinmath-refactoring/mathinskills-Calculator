package com.mathinskills.calculator.data.api

import com.mathinskills.calculator.data.request.CalculateReq
import com.mathinskills.calculator.data.response.CalculationRecordResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path


interface UnitCalcuLateApi {
    @GET("api/auth/me/")
    suspend fun getUserInfo(): Map<String, Any>? // 사용자 정보 API

    @GET("api/calculations/")
    suspend fun getCalculations(
        @Header("Authorization") token: String,
    ): List<CalculationRecordResponse>? // 계산 기록 리스트

        @POST("api/calculations/")
        suspend fun postCalculations(
            @Header("Authorization") token: String,
            @Body calculateReq: CalculateReq,
        ): Any  // 계산 기록


    @DELETE("api/calculations/{id}/")
    suspend fun deleteCalculations(
        @Header("Authorization") token: String,
        @Path("id") recordId: Int,
    )



}
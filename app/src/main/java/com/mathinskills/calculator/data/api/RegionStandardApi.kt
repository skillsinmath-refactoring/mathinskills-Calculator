package com.mathinskills.calculator.data.api

import com.mathinskills.calculator.data.response.RegionStandard
import retrofit2.http.GET
import retrofit2.http.Query

interface RegionStandardApi {
    @GET("/api/standards/")
    suspend fun getRegionStandard(// 교육청별 기준정보 조회
        @Query("region_code") regionCode: String? = null,
    ): List<RegionStandard>


}
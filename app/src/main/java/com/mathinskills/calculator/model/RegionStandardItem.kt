package com.mathinskills.calculator.model

data class RegionStandardItem(
    val id: Int,
    val regionCode: String,
    val educationOffice: String,
    val courseType: String?,         // nullable
    val standardPrice: String,
    val effectiveDate: String,       // 날짜를 String으로 관리 (ex: "2025-09-10")
    val sourceUrl: String?           // nullable
)

package com.mathinskills.calculator.data.response

data class CalculationRecordResponse(
    val id: Int,
    val user: Int,
    val course_type: String?,
    val education_office: String,
    val subject: String,
    val minutes_per_class: Int,
    val lessons_per_week: Int,
    val lessons_per_month: Double,
    val tuition_fee: Int,
    val total_minutes: Long,
    val unit_price: String,
    val standard_price_at_calc: Double, //기준 분당단가
    val is_valid: Boolean,
    val created_at: String,
    val updated_at: String
)

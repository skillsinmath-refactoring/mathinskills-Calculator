package com.mathinskills.calculator.data.request

data class CalculateReq(
    val education_office: String,
    val subject: String,
    val minutes_per_class: Int,
    val lessons_per_week: Int,
    val lessons_per_month: Double,
    val tuition_fee: Int,
    val course_type: String?,
    val unit_price: Double,
    val standard_price_at_calc: Double,
    val is_valid: Boolean
)

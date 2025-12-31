package com.mathinskills.calculator.model

data class SubjectItem(
    val id: Int,
    val courseType: String,//교습과정구분
    val educationOffice: String,//교육청
    val subject: String,//과목
    val onceMinutes: Int,//1회 수업 분.
    val tuition_fee: Int,//수업료
    val weekTimes: Int,//주당 횟수
    val monthTimes: Double,
    val rate: Boolean,//결과
    val standardRate: Double, // 기준분당단가
    val myCalculate: Double // 계산해서 나온 분당단가
)
package com.mathinskills.calculator.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculation_table")
data class CalculationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val courseType: String?,
    val educationOffice: String,
    val subject: String,
    val minutesPerClass: Int,
    val lessonsPerWeek: Int,
    val lessonsPerMonth: Double,
    val tuitionFee: Int,
    val totalMinutes: Int = (minutesPerClass * lessonsPerWeek * 4), // 계산 필드
    val unitPrice: Double,
    val standardPriceAtCalc: Double,
    val isValid: Boolean,
    val createdAt: String = System.currentTimeMillis().toString(),
    val updatedAt: String = System.currentTimeMillis().toString()
)


@Entity(tableName = "region_standard_table")
data class RegionStandardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val regionCode: String,
    val educationOffice: String,
    val courseType: String,
    val subjectCategory: String,
    val standardPrice: String,
    val effectiveDate: String,
    val sourceUrl: String
)
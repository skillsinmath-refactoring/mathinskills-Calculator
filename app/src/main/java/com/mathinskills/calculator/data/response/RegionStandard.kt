package com.mathinskills.calculator.data.response

import com.google.gson.annotations.SerializedName

data class RegionStandard(
    val id: Int,
    @SerializedName("region_code") val regionCode: String,
    @SerializedName("education_office") val educationOffice: String,
    @SerializedName("course_type") val courseType: String,
    @SerializedName("subject_category") val subjectCategory: String,
    @SerializedName("standard_price") val standardPrice: String,
    @SerializedName("effective_date") val effectiveDate: String,
    @SerializedName("source_url") val sourceUrl: String
)
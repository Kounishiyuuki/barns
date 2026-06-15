package com.barns.app.domain.model

data class CompanyInfo(
    val id: String,
    val displayName: String,
    val description: String,
    val inquiryPolicy: String,
    val phoneLabel: String,
    val phoneNumber: String?,
    val address: String?,
    val businessHoursNote: String?,
    val imageUrl: String?,
)

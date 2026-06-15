package com.barns.app.domain.model

data class CareGuide(
    val id: String,
    val title: String,
    val categoryId: String,
    val summary: String,
    val steps: List<String>,
    val frequency: String,
    val cautions: List<String>,
    val imageUrl: String?,
)

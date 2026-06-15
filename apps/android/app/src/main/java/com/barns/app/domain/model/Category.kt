package com.barns.app.domain.model

data class Category(
    val id: String,
    val name: String,
    val description: String,
    val colorHex: String,
    val imageUrl: String?,
)

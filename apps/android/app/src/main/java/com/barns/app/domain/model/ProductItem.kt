package com.barns.app.domain.model

import java.time.Instant

data class ProductItem(
    val id: String,
    val categoryId: String,
    val name: String,
    val type: ProductItemType,
    val installedOrPurchasedAt: Instant?,
    val locationLabel: String?,
    val status: ProductItemStatus,
    val careGuideIds: List<String>,
    val notes: String?,
    val imageUrl: String?,
    val updatedAt: Instant?,
)

enum class ProductItemType { INSTALLED, PURCHASED }

enum class ProductItemStatus { ACTIVE, ARCHIVED }

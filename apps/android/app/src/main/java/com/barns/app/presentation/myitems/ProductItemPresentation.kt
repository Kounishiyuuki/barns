package com.barns.app.presentation.myitems

import com.barns.app.domain.model.ProductItem
import com.barns.app.domain.model.ProductItemStatus
import com.barns.app.domain.model.ProductItemType

/**
 * Presentation-only display helper that frames a registered [ProductItem] as
 * the customer's own/installed greenery — closer to a "My Appliances for
 * greenery" registry than a generic care list. Parity with the iOS
 * ProductItemPresentation introduced in PR #27.
 *
 * This is a pure value type derived from existing domain fields only. It adds
 * no new domain model, no persistence, and no data access; it just shapes
 * calm, support-oriented copy for the My Items list and detail screens.
 */
data class ProductItemPresentation(
    val name: String,
    val categoryLabel: String,
    val typeLabel: String,
    val locationLabel: String,
    val statusLabel: String,
    val careStatusLabel: String,
    val nextActionHint: String,
    val ownershipSummary: String,
) {
    companion object {
        fun from(item: ProductItem): ProductItemPresentation {
            val careLinked = item.careGuideIds.isNotEmpty()
            return ProductItemPresentation(
                name = item.name,
                categoryLabel = categoryLabel(item.categoryId),
                typeLabel = typeLabel(item.type),
                locationLabel = item.locationLabel ?: "Location not set",
                statusLabel = when (item.status) {
                    ProductItemStatus.ACTIVE -> "In your care"
                    ProductItemStatus.ARCHIVED -> "Archived"
                },
                careStatusLabel = if (careLinked) {
                    "Care guidance linked"
                } else {
                    "No care guide linked yet"
                },
                nextActionHint = if (careLinked) {
                    "Open care guidance for your next step"
                } else {
                    "Browse care guidance to set up routine care"
                },
                ownershipSummary = ownershipSummary(item.type),
            )
        }

        private fun categoryLabel(categoryId: String): String = when (categoryId) {
            "cat-wall-green" -> "Wall greenery"
            "cat-interior-green" -> "Interior greenery"
            else -> {
                // Fall back to a readable label derived from the id, e.g.
                // "cat-roof-garden" -> "Roof garden".
                val trimmed = categoryId.removePrefix("cat-")
                val words = trimmed.split("-").filter { it.isNotEmpty() }
                if (words.isEmpty()) {
                    "Greenery"
                } else {
                    val first = words.first().replaceFirstChar { it.uppercase() }
                    val rest = words.drop(1).joinToString(" ")
                    if (rest.isEmpty()) first else "$first $rest"
                }
            }
        }

        private fun typeLabel(type: ProductItemType): String = when (type) {
            ProductItemType.INSTALLED -> "Installed greenery"
            ProductItemType.PURCHASED -> "Owned greenery"
        }

        private fun ownershipSummary(type: ProductItemType): String = when (type) {
            ProductItemType.INSTALLED -> "Installed greenery in your care"
            ProductItemType.PURCHASED -> "Greenery you own"
        }
    }
}

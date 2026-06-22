package com.barns.app.presentation.myitems

import com.barns.app.domain.model.CatalogItem
import com.barns.app.domain.model.ProductItemType

/**
 * Presentation-only prefill for the Register Greenery flow, derived from an
 * official [CatalogItem] so the user can start registering greenery they
 * already have. Mirrors the iOS RegisterGreeneryPrefill (PR #41).
 *
 * Boundary: this is a prefill source only. It copies official, non-personal
 * fields (name, category, a compatible type) — never customer-owned state. It
 * does not mutate the CatalogItem, does not link it, and does not create a
 * ProductItem. The user must still confirm and save through the existing
 * Register Greenery flow.
 */
data class RegisterGreeneryPrefill(
    val name: String,
    val categoryId: String,
    val type: ProductItemType,
) {
    companion object {
        fun from(item: CatalogItem): RegisterGreeneryPrefill = RegisterGreeneryPrefill(
            name = item.name,
            categoryId = item.categoryId,
            // Wall greening is typically installed; other greenery is typically
            // owned. This is only an initial value the user can change.
            type = if (item.categoryId == "cat-wall-green") {
                ProductItemType.INSTALLED
            } else {
                ProductItemType.PURCHASED
            },
        )
    }
}

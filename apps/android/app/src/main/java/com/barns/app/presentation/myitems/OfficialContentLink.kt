package com.barns.app.presentation.myitems

/**
 * Temporary mapping from a registered greenery's `categoryId` to the official
 * read-only content (basic info + default care guides) shown as supporting
 * after-care reference.
 *
 * Why this exists: [com.barns.app.domain.model.ProductItem] does not yet carry
 * an explicit `greeneryInfoId` / `catalogItemId`. Rather than rewrite the model
 * now, this resolver provides a small, documented, presentation-layer link by
 * category. The ids mirror shared/mock-data and the official content
 * repositories. When `ProductItem` gains an explicit official-content id,
 * replace this resolver. Mirrors the iOS OfficialContentLink (PR #37).
 *
 * Boundary: pure value mapping only. No customer-owned state, no I/O.
 */
object OfficialContentLink {
    data class Resolved(
        val greeneryInfoId: String?,
        val careGuideIds: List<String>,
    )

    fun resolve(categoryId: String): Resolved = when (categoryId) {
        "cat-wall-green" -> Resolved(
            greeneryInfoId = "greenery-info-wall-green",
            careGuideIds = listOf("guide-wall-green-basic", "guide-cleaning-basic"),
        )
        "cat-interior-green" -> Resolved(
            greeneryInfoId = "greenery-info-interior-foliage",
            careGuideIds = listOf("guide-watering-basic", "guide-planter-sunlight-basic"),
        )
        "cat-maintenance-supply" -> Resolved(
            greeneryInfoId = null,
            careGuideIds = listOf("guide-cleaning-basic"),
        )
        else -> Resolved(greeneryInfoId = null, careGuideIds = emptyList())
    }
}

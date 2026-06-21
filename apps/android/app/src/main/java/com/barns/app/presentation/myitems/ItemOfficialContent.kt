package com.barns.app.presentation.myitems

/**
 * Concise, presentation-only view of the official read-only content shown as
 * supporting after-care reference on a registered greenery's detail screen.
 *
 * Boundary: this is built from official [com.barns.app.domain.model.GreeneryInfo]
 * / [com.barns.app.domain.model.CareGuide] content only. It carries no
 * customer-owned [com.barns.app.domain.model.ProductItem] state and is never
 * submitted anywhere — it is reference information, not personalized data.
 */
data class ItemOfficialContent(
    val overview: String?,
    val lightPreference: String?,
    val wateringOverview: String?,
    val careGuides: List<CareGuideSummary>,
) {
    data class CareGuideSummary(
        val id: String,
        val title: String,
        val summary: String,
    )

    val hasBasicInformation: Boolean
        get() = overview != null || lightPreference != null || wateringOverview != null

    val hasCareGuides: Boolean
        get() = careGuides.isNotEmpty()

    val isEmpty: Boolean
        get() = !hasBasicInformation && !hasCareGuides
}

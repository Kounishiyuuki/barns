package com.barns.app.presentation.catalog

import com.barns.app.domain.model.CatalogItem

/**
 * Presentation-only models for the supporting Catalog experience.
 *
 * Boundary: built from official read-only content ([CatalogItem],
 * GreeneryInfo, CareGuide) only. No customer-owned ProductItem state, no
 * pricing/stock/order fields, nothing submitted. Catalog is supporting
 * reference content, not a shopping flow. Mirrors the iOS Catalog (PR #39).
 */
data class CatalogPresentationItem(
    val id: String,
    val name: String,
    val kindLabel: String,
    val summary: String,
) {
    companion object {
        fun from(item: CatalogItem): CatalogPresentationItem = CatalogPresentationItem(
            id = item.id,
            name = item.name,
            kindLabel = CatalogKind.label(item.kind),
            summary = item.summary,
        )
    }
}

/** Concise Catalog detail content, with optional linked official info. */
data class CatalogDetailContent(
    val name: String,
    val kindLabel: String,
    val summary: String,
    val overview: String?,
    val lightPreference: String?,
    val wateringOverview: String?,
    val careGuides: List<CareGuideSummary>,
    // Prefill for starting a local Register Greenery flow from this item.
    // Built from official fields only; the user still confirms and saves.
    val registerPrefill: com.barns.app.presentation.myitems.RegisterGreeneryPrefill,
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
}

/** Humanizes a `CatalogItem.kind` token (e.g. "wall-greening") for display. */
object CatalogKind {
    fun label(kind: String): String {
        val words = kind.split("-").filter { it.isNotEmpty() }
        if (words.isEmpty()) return "Greenery"
        val first = words.first().replaceFirstChar { it.uppercase() }
        val rest = words.drop(1).joinToString(" ")
        return if (rest.isEmpty()) first else "$first $rest"
    }
}

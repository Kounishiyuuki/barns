package com.barns.app.domain.model

/**
 * Official, read-only catalog content: products / installation / greenery
 * candidates shown as supporting reference, not customer-owned state.
 *
 * Boundary: this is shared official content (same for every user). It must
 * never carry user-owned [ProductItem] state — no location label, personal
 * notes, ownership status, care logs, or consultation drafts. It may only
 * reference official content by id ([greeneryInfoId], [careGuideIds]).
 */
data class CatalogItem(
    val id: String,
    val categoryId: String,
    val name: String,
    val kind: String,
    val summary: String,
    val greeneryInfoId: String?,
    val careGuideIds: List<String>,
    val imageUrl: String?,
)

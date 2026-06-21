package com.barns.app.presentation.myitems

import com.barns.app.domain.model.ProductItem
import com.barns.app.domain.usecase.catalog.GetCareGuidesUseCase
import com.barns.app.domain.usecase.catalog.GetGreeneryInfoUseCase

/**
 * Resolves official basic information and care guides for a registered
 * greenery, through official-content use cases only (never direct repository
 * or JSON access). Centralizes the temporary [OfficialContentLink] mapping so
 * the view model and screen stay free of resolution logic.
 *
 * Linking is intentionally minimal: [ProductItem] has no `greeneryInfoId` yet,
 * so an item is mapped to official content by its `categoryId`. The item's own
 * `careGuideIds` are preferred when present; otherwise the category defaults
 * are used. Failures degrade to `null`. Mirrors the iOS approach (PR #37).
 */
class ItemOfficialContentResolver(
    private val getGreeneryInfoUseCase: GetGreeneryInfoUseCase,
    private val getCareGuidesUseCase: GetCareGuidesUseCase,
) {
    suspend fun resolve(item: ProductItem): ItemOfficialContent? {
        val link = OfficialContentLink.resolve(item.categoryId)

        var overview: String? = null
        var lightPreference: String? = null
        var wateringOverview: String? = null
        link.greeneryInfoId?.let { infoId ->
            runCatching { getGreeneryInfoUseCase.execute(infoId) }.getOrNull()?.let { info ->
                overview = info.overview
                lightPreference = info.lightPreference
                wateringOverview = info.wateringOverview
            }
        }

        val guideIds = item.careGuideIds.ifEmpty { link.careGuideIds }
        val guides = runCatching { getCareGuidesUseCase.execute(guideIds) }.getOrDefault(emptyList())
        val summaries = guides.map {
            ItemOfficialContent.CareGuideSummary(id = it.id, title = it.title, summary = it.summary)
        }

        val content = ItemOfficialContent(
            overview = overview,
            lightPreference = lightPreference,
            wateringOverview = wateringOverview,
            careGuides = summaries,
        )
        return if (content.isEmpty) null else content
    }
}

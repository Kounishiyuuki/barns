package com.barns.app.catalog

import com.barns.app.data.repository.MockCareGuideRepository
import com.barns.app.data.repository.MockCatalogRepository
import com.barns.app.data.repository.MockGreeneryInfoRepository
import com.barns.app.domain.model.CatalogItem
import com.barns.app.domain.usecase.catalog.GetCareGuidesUseCase
import com.barns.app.domain.usecase.catalog.GetCatalogItemDetailUseCase
import com.barns.app.domain.usecase.catalog.GetCatalogItemsUseCase
import com.barns.app.domain.usecase.catalog.GetGreeneryInfoUseCase
import com.barns.app.presentation.catalog.CatalogDetailContent
import com.barns.app.presentation.catalog.CatalogKind
import com.barns.app.presentation.catalog.CatalogPresentationItem
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Smoke tests for the supporting Catalog presentation. Official read-only
 * content is loaded through use cases (not direct mock access), stays separate
 * from customer-owned ProductItem state, and is local-only with no shopping/EC
 * fields. The view models drive resolution through these same use cases.
 */
class CatalogPresentationSmokeTest {

    private val getItems = GetCatalogItemsUseCase(MockCatalogRepository())
    private val getDetail = GetCatalogItemDetailUseCase(MockCatalogRepository())
    private val getInfo = GetGreeneryInfoUseCase(MockGreeneryInfoRepository())
    private val getGuides = GetCareGuidesUseCase(MockCareGuideRepository())

    /** Mirrors CatalogDetailViewModel.resolve, kept deterministic for JVM. */
    private suspend fun resolve(item: CatalogItem): CatalogDetailContent {
        var overview: String? = null
        var light: String? = null
        var watering: String? = null
        item.greeneryInfoId?.let { id ->
            getInfo.execute(id)?.let {
                overview = it.overview
                light = it.lightPreference
                watering = it.wateringOverview
            }
        }
        val guides = if (item.careGuideIds.isEmpty()) emptyList() else getGuides.execute(item.careGuideIds)
        return CatalogDetailContent(
            name = item.name,
            kindLabel = CatalogKind.label(item.kind),
            summary = item.summary,
            overview = overview,
            lightPreference = light,
            wateringOverview = watering,
            careGuides = guides.map {
                CatalogDetailContent.CareGuideSummary(it.id, it.title, it.summary)
            },
            registerPrefill = com.barns.app.presentation.myitems.RegisterGreeneryPrefill.from(item),
        )
    }

    @Test
    fun catalogListPresentationBuildsThroughUseCase() = runTest {
        val items = getItems.execute().map(CatalogPresentationItem::from)

        assertFalse(items.isEmpty())
        val first = items.first()
        assertFalse(first.name.isBlank())
        assertFalse(first.kindLabel.isBlank())
        assertFalse(first.summary.isBlank())
    }

    @Test
    fun catalogDetailResolvesLinkedOfficialContent() = runTest {
        val item = getDetail.execute("catalog-wall-green-panel")
        assertNotNull(item)
        val detail = resolve(item!!)

        assertFalse(detail.name.isBlank())
        assertTrue(detail.hasBasicInformation)
        assertNotNull(detail.overview)
        assertTrue(detail.hasCareGuides)
        assertFalse(detail.careGuides.first().title.isBlank())
    }

    @Test
    fun detailWithoutGreeneryInfoDegradesSafely() = runTest {
        // Maintenance kit has greeneryInfoId == null but one care guide.
        val item = getDetail.execute("catalog-maintenance-kit")!!
        val detail = resolve(item)

        assertFalse(detail.hasBasicInformation)
        assertNull(detail.overview)
        assertTrue(detail.hasCareGuides)
    }

    @Test
    fun unknownCatalogItemIsNull() = runTest {
        assertNull(getDetail.execute("catalog-does-not-exist"))
    }

    @Test
    fun kindLabelHumanizesToken() {
        assertEquals("Wall greening", CatalogKind.label("wall-greening"))
        assertEquals("Interior green", CatalogKind.label("interior-green"))
        assertEquals("Greenery", CatalogKind.label(""))
    }

    @Test
    fun catalogPresentationHasNoShoppingOrOwnedFields() {
        val listFields = CatalogPresentationItem::class.java.declaredFields.map { it.name }.toSet()
        val detailFields = CatalogDetailContent::class.java.declaredFields.map { it.name }.toSet()
        for (forbidden in listOf(
            "price", "cart", "order", "payment", "stock", "checkout",
            "locationLabel", "notes", "status", "consultationDraft",
        )) {
            assertFalse("Forbidden field in list row: $forbidden", listFields.contains(forbidden))
            assertFalse("Forbidden field in detail: $forbidden", detailFields.contains(forbidden))
        }
    }
}

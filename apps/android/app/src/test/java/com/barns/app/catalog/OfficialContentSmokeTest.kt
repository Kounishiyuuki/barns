package com.barns.app.catalog

import com.barns.app.data.repository.MockCareGuideRepository
import com.barns.app.data.repository.MockCatalogRepository
import com.barns.app.data.repository.MockGreeneryInfoRepository
import com.barns.app.domain.model.CatalogItem
import com.barns.app.domain.usecase.catalog.GetCareGuidesUseCase
import com.barns.app.domain.usecase.catalog.GetCatalogItemDetailUseCase
import com.barns.app.domain.usecase.catalog.GetCatalogItemsUseCase
import com.barns.app.domain.usecase.catalog.GetGreeneryInfoUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Smoke tests for the official, read-only content foundation: Catalog,
 * GreeneryInfo, and CareGuide. Local-only, no network, no persistence.
 */
class OfficialContentSmokeTest {

    @Test
    fun catalogItemsAreListed() = runTest {
        val items = GetCatalogItemsUseCase(MockCatalogRepository()).execute()

        assertTrue(items.isNotEmpty())
        // Official content uses only local mock image references (or none) —
        // never remote URLs.
        assertTrue(items.all { it.imageUrl == null || it.imageUrl!!.startsWith("mock://") })
    }

    @Test
    fun catalogItemReferencesResolve() = runTest {
        val getDetail = GetCatalogItemDetailUseCase(MockCatalogRepository())
        val getInfo = GetGreeneryInfoUseCase(MockGreeneryInfoRepository())
        val getGuides = GetCareGuidesUseCase(MockCareGuideRepository())

        val item = getDetail.execute("catalog-wall-green-panel")
        assertNotNull(item)
        requireNotNull(item)

        // greeneryInfoId resolves when present.
        val infoId = requireNotNull(item.greeneryInfoId)
        assertNotNull(getInfo.execute(infoId))

        // careGuideIds resolve.
        assertFalse(item.careGuideIds.isEmpty())
        val guides = getGuides.execute(item.careGuideIds)
        assertEquals(item.careGuideIds.size, guides.size)
    }

    @Test
    fun everyCatalogReferenceResolves() = runTest {
        val items = GetCatalogItemsUseCase(MockCatalogRepository()).execute()
        val getInfo = GetGreeneryInfoUseCase(MockGreeneryInfoRepository())
        val getGuides = GetCareGuidesUseCase(MockCareGuideRepository())

        for (item in items) {
            item.greeneryInfoId?.let { infoId ->
                assertNotNull("Missing GreeneryInfo for $infoId", getInfo.execute(infoId))
            }
            val guides = getGuides.execute(item.careGuideIds)
            assertEquals("Unresolved care guide for ${item.id}", item.careGuideIds.size, guides.size)
        }
    }

    @Test
    fun greeneryInfoUnknownIdReturnsNull() = runTest {
        val getInfo = GetGreeneryInfoUseCase(MockGreeneryInfoRepository())
        assertNull(getInfo.execute("greenery-info-does-not-exist"))
    }

    @Test
    fun careGuidesListLookupAndSubset() = runTest {
        val repo = MockCareGuideRepository()

        assertTrue(repo.careGuides().isNotEmpty())
        assertEquals("guide-watering-basic", repo.careGuide("guide-watering-basic")?.id)
        assertNull(repo.careGuide("guide-missing"))

        val subset = repo.careGuides(listOf("guide-cleaning-basic", "missing-id"))
        assertEquals(listOf("guide-cleaning-basic"), subset.map { it.id })
    }

    /**
     * Guards the data boundary: CatalogItem must not carry user-owned state.
     * If any forbidden field existed on the data class, this would fail.
     */
    @Test
    fun catalogItemHasNoCustomerOwnedFields() {
        val fields = CatalogItem::class.java.declaredFields.map { it.name }.toSet()
        // Official content fields are present.
        for (expected in listOf(
            "id", "categoryId", "name", "kind", "summary", "greeneryInfoId", "careGuideIds", "imageUrl",
        )) {
            assertTrue("Missing official field: $expected", fields.contains(expected))
        }
        // User-owned state is not part of CatalogItem.
        for (forbidden in listOf("locationLabel", "notes", "status", "careLogs", "consultationDraft")) {
            assertFalse("Customer-owned field leaked: $forbidden", fields.contains(forbidden))
        }
    }
}

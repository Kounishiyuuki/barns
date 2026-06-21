package com.barns.app.myitems

import com.barns.app.data.repository.MockCareGuideRepository
import com.barns.app.data.repository.MockGreeneryInfoRepository
import com.barns.app.domain.model.ProductItem
import com.barns.app.domain.model.ProductItemStatus
import com.barns.app.domain.model.ProductItemType
import com.barns.app.domain.usecase.catalog.GetCareGuidesUseCase
import com.barns.app.domain.usecase.catalog.GetGreeneryInfoUseCase
import com.barns.app.presentation.myitems.ItemOfficialContent
import com.barns.app.presentation.myitems.ItemOfficialContentResolver
import com.barns.app.presentation.myitems.OfficialContentLink
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Smoke tests for the My Greenery detail official-content sections. Official
 * read-only content is resolved through use cases (not direct mock access),
 * stays separate from customer-owned ProductItem state, and is local-only.
 */
class ItemOfficialContentSmokeTest {

    private fun resolver() = ItemOfficialContentResolver(
        getGreeneryInfoUseCase = GetGreeneryInfoUseCase(MockGreeneryInfoRepository()),
        getCareGuidesUseCase = GetCareGuidesUseCase(MockCareGuideRepository()),
    )

    private fun item(
        id: String = "item-1",
        categoryId: String,
        careGuideIds: List<String> = emptyList(),
    ) = ProductItem(
        id = id,
        categoryId = categoryId,
        name = "Sample greenery",
        type = ProductItemType.INSTALLED,
        installedOrPurchasedAt = null,
        locationLabel = "Entrance wall",
        status = ProductItemStatus.ACTIVE,
        careGuideIds = careGuideIds,
        notes = null,
        imageUrl = null,
        updatedAt = null,
    )

    @Test
    fun wallGreenItemResolvesBasicInfoAndCareGuides() = runTest {
        val official = resolver().resolve(item(categoryId = "cat-wall-green"))

        assertNotNull(official)
        requireNotNull(official)
        // Basic information resolved via the GreeneryInfo use case.
        assertTrue(official.hasBasicInformation)
        assertNotNull(official.overview)
        assertNotNull(official.lightPreference)
        assertNotNull(official.wateringOverview)
        // Care guide summaries resolved via the care-guide use case.
        assertTrue(official.hasCareGuides)
        assertFalse(official.careGuides.first().title.isBlank())
    }

    @Test
    fun careGuidesResolveFromCategoryDefaultsWhenItemHasNone() = runTest {
        // Empty item.careGuideIds -> category defaults supply official guides,
        // proving resolution is not read from ProductItem state.
        val official = resolver().resolve(item(categoryId = "cat-interior-green"))

        val guides = official?.careGuides ?: emptyList()
        assertFalse(guides.isEmpty())
        assertTrue(guides.any { it.id == "guide-watering-basic" })
    }

    @Test
    fun itemOwnCareGuideIdsArePreferredWhenPresent() = runTest {
        val official = resolver().resolve(
            item(categoryId = "cat-interior-green", careGuideIds = listOf("guide-cleaning-basic")),
        )

        val ids = official?.careGuides?.map { it.id } ?: emptyList()
        assertEquals(listOf("guide-cleaning-basic"), ids)
    }

    @Test
    fun unknownCategoryDegradesToNoOfficialContent() = runTest {
        val official = resolver().resolve(item(categoryId = "cat-unknown"))
        assertNull(official)
    }

    @Test
    fun officialContentLinkResolverIsCategoryBased() {
        val wall = OfficialContentLink.resolve("cat-wall-green")
        assertEquals("greenery-info-wall-green", wall.greeneryInfoId)
        assertFalse(wall.careGuideIds.isEmpty())

        val supply = OfficialContentLink.resolve("cat-maintenance-supply")
        assertNull(supply.greeneryInfoId)
        assertFalse(supply.careGuideIds.isEmpty())

        val unknown = OfficialContentLink.resolve("cat-unknown")
        assertNull(unknown.greeneryInfoId)
        assertTrue(unknown.careGuideIds.isEmpty())
    }

    @Test
    fun officialContentHasNoCustomerOwnedFields() {
        val fields = ItemOfficialContent::class.java.declaredFields.map { it.name }.toSet()
        for (forbidden in listOf("locationLabel", "notes", "status", "productItemId")) {
            assertFalse("Customer-owned field leaked: $forbidden", fields.contains(forbidden))
        }
    }
}

package com.barns.app.myitems

import com.barns.app.data.repository.MockProductItemRepository
import com.barns.app.domain.model.ProductItem
import com.barns.app.domain.model.ProductItemStatus
import com.barns.app.domain.model.ProductItemType
import com.barns.app.domain.usecase.myitems.AddProductItemUseCase
import com.barns.app.domain.usecase.myitems.GetProductItemsUseCase
import com.barns.app.presentation.myitems.ProductItemPresentation
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Local-first My Items smoke tests: seeded items are listed and a new item is
 * appended to the in-memory store. No network, no persistence.
 */
class MyItemsSmokeTest {

    @Test
    fun seededItemsAreListed() = runTest {
        val repository = MockProductItemRepository()
        val getItems = GetProductItemsUseCase(repository)

        val items = getItems.execute()

        assertEquals(2, items.size)
    }

    @Test
    fun addItemAppendsLocally() = runTest {
        val repository = MockProductItemRepository()
        val addItem = AddProductItemUseCase(repository)
        val getItems = GetProductItemsUseCase(repository)

        val before = getItems.execute().size
        val created = addItem.execute(
            name = "New planter",
            categoryId = "cat-interior-green",
            locationLabel = null,
            notes = null,
        )
        val after = getItems.execute()

        assertEquals(before + 1, after.size)
        assertTrue(after.any { it.id == created.id })
        // Image fields stay optional for the MVP.
        assertNull(created.imageUrl)
    }

    @Test
    fun installedItemPresentationFramesOwnershipAndSupport() {
        val item = ProductItem(
            id = "item-1",
            categoryId = "cat-wall-green",
            name = "Lobby wall greenery",
            type = ProductItemType.INSTALLED,
            installedOrPurchasedAt = null,
            locationLabel = "Entrance wall",
            status = ProductItemStatus.ACTIVE,
            careGuideIds = emptyList(),
            notes = null,
            imageUrl = null,
            updatedAt = null,
        )

        val display = ProductItemPresentation.from(item)

        assertEquals("Installed greenery in your care", display.ownershipSummary)
        assertEquals("Installed greenery", display.typeLabel)
        assertEquals("Wall greenery", display.categoryLabel)
        assertEquals("Entrance wall", display.locationLabel)
        assertEquals("In your care", display.statusLabel)
        // No linked care guide yet -> next-action guidance is offered.
        assertEquals("No care guide linked yet", display.careStatusLabel)
        assertFalse(display.nextActionHint.isBlank())
    }

    @Test
    fun presentationFallsBackForMissingLocationAndUnknownCategory() {
        val item = ProductItem(
            id = "item-2",
            categoryId = "cat-roof-garden",
            name = "Rooftop planter",
            type = ProductItemType.PURCHASED,
            installedOrPurchasedAt = null,
            locationLabel = null,
            status = ProductItemStatus.ACTIVE,
            careGuideIds = listOf("guide-1"),
            notes = null,
            imageUrl = null,
            updatedAt = null,
        )

        val display = ProductItemPresentation.from(item)

        assertEquals("Greenery you own", display.ownershipSummary)
        assertEquals("Roof garden", display.categoryLabel)
        assertEquals("Location not set", display.locationLabel)
        assertEquals("Care guidance linked", display.careStatusLabel)
    }
}

package com.barns.app.myitems

import com.barns.app.data.repository.MockProductItemRepository
import com.barns.app.domain.model.ProductItemStatus
import com.barns.app.domain.model.ProductItemType
import com.barns.app.domain.usecase.myitems.AddProductItemUseCase
import com.barns.app.domain.usecase.myitems.GetProductItemsUseCase
import com.barns.app.domain.usecase.myitems.UpdateProductItemUseCase
import com.barns.app.presentation.myitems.EditGreeneryViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Smoke tests for the local-only Edit Greenery flow. Editing updates the
 * customer-owned ProductItem locally only; official content is untouched and
 * nothing is saved until an explicit save.
 */
class EditGreenerySmokeTest {

    private suspend fun seededItem(repository: MockProductItemRepository) =
        GetProductItemsUseCase(repository).execute().first()

    @Test
    fun editFormInitializesFromExistingItem() = runTest {
        val repository = MockProductItemRepository()
        val item = seededItem(repository)

        val viewModel = EditGreeneryViewModel(item, UpdateProductItemUseCase(repository))
        val state = viewModel.state.value

        assertEquals(item.name, state.name)
        assertEquals(item.type, state.type)
        assertEquals(item.locationLabel ?: "", state.locationLabel)
        assertEquals(item.status, state.status)
        assertTrue(state.canSave)
    }

    @Test
    fun editingFieldsDoesNotSaveUntilExplicitSave() = runTest {
        val repository = MockProductItemRepository()
        val item = seededItem(repository)

        val viewModel = EditGreeneryViewModel(item, UpdateProductItemUseCase(repository))
        viewModel.onNameChange("Renamed greenery")
        viewModel.onLocationChange("New spot")

        // No save() called yet: the stored item is unchanged.
        val stored = repository.productItem(item.id)
        assertEquals(item.name, stored?.name)
        assertNotEquals("Renamed greenery", stored?.name)
    }

    @Test
    fun explicitUpdateUpdatesLocalItemAndKeepsCountStable() = runTest {
        val repository = MockProductItemRepository()
        val item = seededItem(repository)
        val getItems = GetProductItemsUseCase(repository)
        val before = getItems.execute().size

        val updated = UpdateProductItemUseCase(repository).execute(
            original = item,
            name = "Renamed greenery",
            type = ProductItemType.PURCHASED,
            locationLabel = "New spot",
            notes = "Edited note",
            status = ProductItemStatus.ARCHIVED,
        )

        val stored = repository.productItem(item.id)
        assertEquals("Renamed greenery", stored?.name)
        assertEquals("New spot", stored?.locationLabel)
        assertEquals("Edited note", stored?.notes)
        assertEquals(ProductItemStatus.ARCHIVED, stored?.status)
        // Update, not insert: count stable; official links preserved.
        assertEquals(before, getItems.execute().size)
        assertEquals(item.id, updated.id)
        assertEquals(item.categoryId, updated.categoryId)
        assertEquals(item.careGuideIds, updated.careGuideIds)
        // The local mock image reference is preserved through an edit (never
        // mutated or dropped); it is a local `mock://` reference, not remote.
        assertEquals(item.imageUrl, updated.imageUrl)
        assertTrue(updated.imageUrl == null || updated.imageUrl!!.startsWith("mock://"))
    }

    @Test
    fun registerGreeneryFlowStillAddsNewItem() = runTest {
        // Editing must not break the existing add path.
        val repository = MockProductItemRepository()
        val getItems = GetProductItemsUseCase(repository)
        val before = getItems.execute().size

        val added = AddProductItemUseCase(repository).execute(
            name = "Brand new",
            categoryId = "cat-wall-green",
            type = ProductItemType.INSTALLED,
            locationLabel = null,
            notes = null,
        )

        val after = getItems.execute()
        assertEquals(before + 1, after.size)
        assertTrue(after.any { it.id == added.id })
    }
}

package com.barns.app.myitems

import com.barns.app.data.repository.MockProductItemRepository
import com.barns.app.domain.model.ProductItemStatus
import com.barns.app.domain.usecase.myitems.ArchiveProductItemUseCase
import com.barns.app.domain.usecase.myitems.GetProductItemsUseCase
import com.barns.app.domain.usecase.myitems.UpdateProductItemUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Smoke tests for the local-only Archive Greenery flow. Archiving is a soft
 * action: the ProductItem stays in the local store (no hard delete), only its
 * status changes, official links are preserved, and the active list excludes
 * archived items.
 */
class ArchiveGreenerySmokeTest {

    @Test
    fun archiveKeepsItemInStoreAndSetsStatus() = runTest {
        val repository = MockProductItemRepository()
        val item = repository.productItems().first()
        val countBefore = repository.productItems().size

        val archived = ArchiveProductItemUseCase(repository).execute(item)

        assertEquals(ProductItemStatus.ARCHIVED, archived.status)
        // No hard delete: the item is still present, count unchanged.
        assertEquals(countBefore, repository.productItems().size)
        val stored = repository.productItem(item.id)
        assertNotNull(stored)
        assertEquals(ProductItemStatus.ARCHIVED, stored?.status)
    }

    @Test
    fun archivePreservesOfficialLinksAndIdentity() = runTest {
        val repository = MockProductItemRepository()
        val item = repository.productItems().first()

        val archived = ArchiveProductItemUseCase(repository).execute(item)

        assertEquals(item.id, archived.id)
        assertEquals(item.categoryId, archived.categoryId)
        assertEquals(item.careGuideIds, archived.careGuideIds)
        assertEquals(item.imageUrl, archived.imageUrl)
        assertEquals(item.name, archived.name)
    }

    @Test
    fun activeListFilterExcludesArchivedItems() = runTest {
        // Mirrors MyItemsViewModel's active filter (the VM applies it on load).
        val repository = MockProductItemRepository()
        val item = repository.productItems().first()
        val getItems = GetProductItemsUseCase(repository)

        val activeBefore = getItems.execute().filter { it.status == ProductItemStatus.ACTIVE }
        assertTrue(activeBefore.any { it.id == item.id })

        ArchiveProductItemUseCase(repository).execute(item)

        val all = getItems.execute()
        val activeAfter = all.filter { it.status == ProductItemStatus.ACTIVE }
        // Still in the store (read-all), but excluded from the active list.
        assertTrue(all.any { it.id == item.id })
        assertFalse(activeAfter.any { it.id == item.id })
    }

    @Test
    fun editFlowStillUpdatesItem() = runTest {
        // Archive must not break the existing edit path.
        val repository = MockProductItemRepository()
        val item = repository.productItems().first()

        val updated = UpdateProductItemUseCase(repository).execute(
            original = item,
            name = "Edited",
            type = item.type,
            locationLabel = item.locationLabel,
            notes = item.notes,
            status = item.status,
        )

        assertEquals("Edited", updated.name)
        assertEquals("Edited", repository.productItem(item.id)?.name)
        assertNull(updated.imageUrl)
    }
}

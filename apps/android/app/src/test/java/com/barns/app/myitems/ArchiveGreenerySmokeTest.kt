package com.barns.app.myitems

import com.barns.app.data.repository.MockCareGuideRepository
import com.barns.app.data.repository.MockGreeneryInfoRepository
import com.barns.app.data.repository.MockProductItemRepository
import com.barns.app.domain.model.ProductItemStatus
import com.barns.app.domain.usecase.catalog.GetCareGuidesUseCase
import com.barns.app.domain.usecase.catalog.GetGreeneryInfoUseCase
import com.barns.app.domain.usecase.myitems.ArchiveProductItemUseCase
import com.barns.app.domain.usecase.myitems.GetProductItemDetailUseCase
import com.barns.app.domain.usecase.myitems.GetProductItemsUseCase
import com.barns.app.domain.usecase.myitems.UpdateProductItemUseCase
import com.barns.app.presentation.myitems.ItemDetailViewModel
import com.barns.app.presentation.myitems.ItemOfficialContentResolver
import com.barns.app.presentation.myitems.MyItemsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Smoke tests for the local-only Archive Greenery flow. Archiving is a soft
 * action: the ProductItem stays in the local store (no hard delete), only its
 * status changes, official links are preserved, and the active list excludes
 * archived items.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ArchiveGreenerySmokeTest {

    // viewModelScope dispatches on Main; route it to a test dispatcher so the
    // real view model load()/archive() coroutines run within these unit tests.
    @Before
    fun setUpMainDispatcher() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDownMainDispatcher() {
        Dispatchers.resetMain()
    }

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
        // The seed's local mock image reference is preserved through the edit.
        assertEquals(item.imageUrl, updated.imageUrl)
    }

    @Test
    fun myItemsViewModelLoadExcludesArchivedItems() = runTest {
        // One seed item is archived; the other stays active.
        val repository = MockProductItemRepository()
        val all = repository.productItems()
        val activeItem = all[0]
        val archivedItem = all[1]
        ArchiveProductItemUseCase(repository).execute(archivedItem)

        val viewModel = MyItemsViewModel(GetProductItemsUseCase(repository))
        viewModel.load()

        // Assert against the view model's own filtered state (not a local filter).
        val state = viewModel.state.value
        assertTrue(state is MyItemsViewModel.State.Loaded)
        val items = (state as MyItemsViewModel.State.Loaded).items
        assertTrue(items.any { it.id == activeItem.id })
        assertFalse(items.any { it.id == archivedItem.id })
        assertTrue(items.all { it.status == ProductItemStatus.ACTIVE })
    }

    @Test
    fun itemDetailViewModelArchivesOnlyOnExplicitAction() = runTest {
        val repository = MockProductItemRepository()
        val target = repository.productItems().first()
        val countBefore = repository.productItems().size
        val viewModel = ItemDetailViewModel(
            itemId = target.id,
            getProductItemDetailUseCase = GetProductItemDetailUseCase(repository),
            officialContentResolver = ItemOfficialContentResolver(
                getGreeneryInfoUseCase = GetGreeneryInfoUseCase(MockGreeneryInfoRepository()),
                getCareGuidesUseCase = GetCareGuidesUseCase(MockCareGuideRepository()),
            ),
            archiveProductItemUseCase = ArchiveProductItemUseCase(repository),
        )

        // Opening the detail does not archive anything.
        viewModel.load()
        assertTrue(viewModel.state.value is ItemDetailViewModel.State.Loaded)
        assertEquals(ProductItemStatus.ACTIVE, repository.productItem(target.id)?.status)

        // Only the explicit archive action changes the status.
        var returnedToList = false
        viewModel.archive(onArchived = { returnedToList = true })

        val stored = repository.productItem(target.id)
        assertEquals(ProductItemStatus.ARCHIVED, stored?.status)
        assertTrue(returnedToList)
        // No hard delete: count stable, official links and identity preserved.
        assertEquals(countBefore, repository.productItems().size)
        assertEquals(target.id, stored?.id)
        assertEquals(target.categoryId, stored?.categoryId)
        assertEquals(target.careGuideIds, stored?.careGuideIds)
        assertEquals(target.imageUrl, stored?.imageUrl)
    }
}

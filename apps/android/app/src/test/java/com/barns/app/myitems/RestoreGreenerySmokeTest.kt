package com.barns.app.myitems

import com.barns.app.data.repository.MockCareGuideRepository
import com.barns.app.data.repository.MockGreeneryInfoRepository
import com.barns.app.data.repository.MockProductItemRepository
import com.barns.app.domain.model.ProductItem
import com.barns.app.domain.model.ProductItemStatus
import com.barns.app.domain.usecase.myitems.ArchiveProductItemUseCase
import com.barns.app.domain.usecase.myitems.GetProductItemsUseCase
import com.barns.app.domain.usecase.myitems.RestoreProductItemUseCase
import com.barns.app.presentation.myitems.ArchivedGreeneryViewModel
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Smoke tests for the local-only Archived list + Restore flow. Restore is the
 * inverse soft action of archive: the ProductItem stays in the local store (no
 * hard delete, no re-create/re-insert), only its status flips from ARCHIVED
 * back to ACTIVE, official links and stable fields are preserved, and restore
 * happens only on an explicit action. Official Catalog / GreeneryInfo /
 * CareGuide content is never mutated.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class RestoreGreenerySmokeTest {

    @Before
    fun setUpMainDispatcher() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDownMainDispatcher() {
        Dispatchers.resetMain()
    }

    private suspend fun archiveFirst(repository: MockProductItemRepository): ProductItem {
        val item = repository.productItems().first()
        return ArchiveProductItemUseCase(repository).execute(item)
    }

    @Test
    fun archivedListLoadsOnlyArchivedItems() = runTest {
        val repository = MockProductItemRepository()
        val archived = archiveFirst(repository)
        val viewModel = ArchivedGreeneryViewModel(
            getProductItemsUseCase = GetProductItemsUseCase(repository),
            restoreProductItemUseCase = RestoreProductItemUseCase(repository),
        )

        viewModel.load()

        val state = viewModel.state.value
        assertTrue(state is ArchivedGreeneryViewModel.State.Loaded)
        val items = (state as ArchivedGreeneryViewModel.State.Loaded).items
        assertTrue(items.any { it.id == archived.id })
        assertTrue(items.all { it.status == ProductItemStatus.ARCHIVED })
    }

    @Test
    fun restoreSetsStatusBackToActiveOnlyOnExplicitAction() = runTest {
        val repository = MockProductItemRepository()
        val archived = archiveFirst(repository)
        val viewModel = ArchivedGreeneryViewModel(
            getProductItemsUseCase = GetProductItemsUseCase(repository),
            restoreProductItemUseCase = RestoreProductItemUseCase(repository),
        )

        // Loading the archived list does not restore anything.
        viewModel.load()
        assertEquals(ProductItemStatus.ARCHIVED, repository.productItem(archived.id)?.status)

        // Only the explicit restore action flips the status back to active.
        viewModel.restore(archived)
        assertEquals(ProductItemStatus.ACTIVE, repository.productItem(archived.id)?.status)
    }

    @Test
    fun restoreDoesNotChangeItemCount() = runTest {
        val repository = MockProductItemRepository()
        val countBefore = repository.productItems().size
        val archived = archiveFirst(repository)

        RestoreProductItemUseCase(repository).execute(archived)

        // No hard delete and no re-insertion: count is stable across the
        // archive + restore round trip.
        assertEquals(countBefore, repository.productItems().size)
    }

    @Test
    fun restorePreservesStableFields() = runTest {
        val repository = MockProductItemRepository()
        val archived = archiveFirst(repository)

        val restored = RestoreProductItemUseCase(repository).execute(archived)

        assertEquals(ProductItemStatus.ACTIVE, restored.status)
        assertEquals(archived.id, restored.id)
        assertEquals(archived.categoryId, restored.categoryId)
        assertEquals(archived.name, restored.name)
        assertEquals(archived.type, restored.type)
        assertEquals(archived.installedOrPurchasedAt, restored.installedOrPurchasedAt)
        assertEquals(archived.locationLabel, restored.locationLabel)
        assertEquals(archived.careGuideIds, restored.careGuideIds)
        assertEquals(archived.notes, restored.notes)
        assertEquals(archived.imageUrl, restored.imageUrl)
    }

    @Test
    fun activeListIncludesAndArchivedListExcludesRestoredItemAfterReload() = runTest {
        val repository = MockProductItemRepository()
        val archived = archiveFirst(repository)
        val activeList = MyItemsViewModel(GetProductItemsUseCase(repository))
        val archivedList = ArchivedGreeneryViewModel(
            getProductItemsUseCase = GetProductItemsUseCase(repository),
            restoreProductItemUseCase = RestoreProductItemUseCase(repository),
        )

        // Before restore: archived list contains it, active list does not.
        activeList.load()
        archivedList.load()
        val activeBefore = (activeList.state.value as MyItemsViewModel.State.Loaded).items
        val archivedBefore = (archivedList.state.value as ArchivedGreeneryViewModel.State.Loaded).items
        assertFalse(activeBefore.any { it.id == archived.id })
        assertTrue(archivedBefore.any { it.id == archived.id })

        // Restore through the archived list view model, then reload both.
        archivedList.restore(archived)
        activeList.load()

        val activeAfter = (activeList.state.value as MyItemsViewModel.State.Loaded).items
        val archivedAfter = (archivedList.state.value as ArchivedGreeneryViewModel.State.Loaded).items
        assertTrue(activeAfter.any { it.id == archived.id })
        assertTrue(activeAfter.all { it.status == ProductItemStatus.ACTIVE })
        assertFalse(archivedAfter.any { it.id == archived.id })
    }

    @Test
    fun restoreDoesNotMutateOfficialContent() = runTest {
        val productRepository = MockProductItemRepository()
        val infoRepository = MockGreeneryInfoRepository()
        val careGuideRepository = MockCareGuideRepository()

        val infoBefore = infoRepository.greeneryInfo("greenery-info-wall-green")
        val guidesBefore = careGuideRepository.careGuides()

        val archived = archiveFirst(productRepository)
        RestoreProductItemUseCase(productRepository).execute(archived)

        // Official read-only content is untouched by a local restore (the
        // restore path only ever touches the ProductItem repository).
        assertEquals(infoBefore, infoRepository.greeneryInfo("greenery-info-wall-green"))
        assertEquals(guidesBefore, careGuideRepository.careGuides())
    }

    @Test
    fun existingArchiveFlowStillWorks() = runTest {
        // Restore must not break the existing archive path.
        val repository = MockProductItemRepository()
        val item = repository.productItems().first()

        val archived = ArchiveProductItemUseCase(repository).execute(item)

        assertEquals(ProductItemStatus.ARCHIVED, archived.status)
        val stored = repository.productItem(item.id)
        assertNotNull(stored)
        assertEquals(ProductItemStatus.ARCHIVED, stored?.status)
    }
}

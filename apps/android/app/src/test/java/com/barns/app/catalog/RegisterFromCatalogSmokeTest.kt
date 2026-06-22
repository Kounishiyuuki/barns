package com.barns.app.catalog

import com.barns.app.data.repository.MockProductItemRepository
import com.barns.app.domain.model.CatalogItem
import com.barns.app.domain.model.ProductItemType
import com.barns.app.domain.usecase.myitems.AddProductItemUseCase
import com.barns.app.domain.usecase.myitems.GetProductItemsUseCase
import com.barns.app.presentation.myitems.AddItemViewModel
import com.barns.app.presentation.myitems.RegisterGreeneryPrefill
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Smoke tests for the local-only "Register from Catalog" prefill flow.
 * CatalogItem is a prefill source only; no customer-owned state is copied,
 * nothing is saved without an explicit save call, and no EC behavior exists.
 */
class RegisterFromCatalogSmokeTest {

    private fun wallGreenCatalogItem() = CatalogItem(
        id = "catalog-wall-green-panel",
        categoryId = "cat-wall-green",
        name = "Lobby wall greenery panel",
        kind = "wall-greening",
        summary = "Official reference",
        greeneryInfoId = "greenery-info-wall-green",
        careGuideIds = listOf("guide-wall-green-basic"),
        imageUrl = null,
    )

    @Test
    fun prefillCopiesOfficialFieldsOnly() {
        val item = wallGreenCatalogItem()
        val prefill = RegisterGreeneryPrefill.from(item)

        assertEquals(item.name, prefill.name)
        assertEquals(item.categoryId, prefill.categoryId)
        // Wall greening maps to installed as an initial, editable value.
        assertEquals(ProductItemType.INSTALLED, prefill.type)
    }

    @Test
    fun prefillTypeForNonWallGreenIsOwned() {
        val item = wallGreenCatalogItem().copy(
            id = "catalog-desk-planter",
            categoryId = "cat-interior-green",
            kind = "interior-green",
        )
        assertEquals(ProductItemType.PURCHASED, RegisterGreeneryPrefill.from(item).type)
    }

    @Test
    fun prefillHasNoCustomerOwnedFields() {
        val fields = RegisterGreeneryPrefill::class.java.declaredFields.map { it.name }.toSet()
        for (forbidden in listOf("locationLabel", "notes", "status", "careLogs", "consultationDraft", "imageUrl")) {
            assertFalse("Customer-owned/official field leaked: $forbidden", fields.contains(forbidden))
        }
    }

    @Test
    fun addItemViewModelAppliesPrefillButDoesNotAutoSave() = runTest {
        val repository = MockProductItemRepository()
        val getItems = GetProductItemsUseCase(repository)
        val before = getItems.execute().size

        val viewModel = AddItemViewModel(
            addProductItemUseCase = AddProductItemUseCase(repository),
            prefill = RegisterGreeneryPrefill.from(wallGreenCatalogItem()),
        )

        // Prefill is applied to the editable form state.
        assertEquals("Lobby wall greenery panel", viewModel.state.value.name)
        assertEquals(ProductItemType.INSTALLED, viewModel.state.value.type)
        assertTrue(viewModel.state.value.canSave)

        // Constructing the view model must NOT have saved anything.
        assertEquals(before, getItems.execute().size)
    }

    @Test
    fun explicitUseCaseSavePersistsLocallyWithPrefilledCategory() = runTest {
        // The Register Greenery save path writes only when explicitly invoked.
        val repository = MockProductItemRepository()
        val getItems = GetProductItemsUseCase(repository)
        val addItem = AddProductItemUseCase(repository)
        val before = getItems.execute().size
        val prefill = RegisterGreeneryPrefill.from(wallGreenCatalogItem())

        val created = addItem.execute(
            name = prefill.name,
            categoryId = prefill.categoryId,
            type = prefill.type,
            locationLabel = null,
            notes = null,
        )

        val after = getItems.execute()
        assertEquals(before + 1, after.size)
        assertEquals("cat-wall-green", created.categoryId)
        assertEquals(ProductItemType.INSTALLED, created.type)
        assertNull(created.imageUrl)
    }
}

package com.barns.app.support

import com.barns.app.data.repository.MockConsultationDraftRepository
import com.barns.app.domain.model.ConsultationCategory
import com.barns.app.domain.model.ConsultationDraftStatus
import com.barns.app.domain.model.ConsultationUrgency
import com.barns.app.domain.model.ProductItem
import com.barns.app.domain.model.ProductItemStatus
import com.barns.app.domain.model.ProductItemType
import com.barns.app.domain.usecase.support.GetConsultationDraftUseCase
import com.barns.app.domain.usecase.support.SaveConsultationDraftUseCase
import com.barns.app.presentation.support.ConsultationDraftViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Local-first consultation draft smoke tests, including starting a draft from
 * a registered greenery item. No network, no submission.
 */
class ConsultationDraftSmokeTest {

    private fun sampleItem() = ProductItem(
        id = "item-wall-green-001",
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

    @Test
    fun saveDraftLinksProductItemLocally() = runTest {
        val repository = MockConsultationDraftRepository()
        val save = SaveConsultationDraftUseCase(repository)

        val draft = save.execute(
            existing = null,
            productItemId = "item-wall-green-001",
            topic = "Leaves drooping",
            category = ConsultationCategory.CARE,
            urgency = ConsultationUrgency.NORMAL,
            body = "Some context",
        )

        assertEquals("item-wall-green-001", draft.productItemId)
        // Drafts stay in draft status; there is no submitted state.
        assertEquals(ConsultationDraftStatus.DRAFT, draft.status)
        assertNull(draft.imageUrl)
    }

    @Test
    fun itemContextViewModelPrefillsFromGreenery() {
        val repository = MockConsultationDraftRepository()
        val item = sampleItem()
        val viewModel = ConsultationDraftViewModel(
            getConsultationDraftUseCase = GetConsultationDraftUseCase(repository),
            saveConsultationDraftUseCase = SaveConsultationDraftUseCase(repository),
            item = item,
        )

        // The note is contextualized with the registered greenery.
        assertEquals(item.name, viewModel.itemContextName)

        // Item-context prefill is synchronous (no coroutine), so state is ready.
        viewModel.load()
        assertTrue(viewModel.state.value.topic.contains(item.name))
        assertTrue(viewModel.state.value.body.contains(item.name))
        assertTrue(viewModel.state.value.canSave)
    }

    @Test
    fun generalDraftHasNoProductItem() = runTest {
        val repository = MockConsultationDraftRepository()
        val save = SaveConsultationDraftUseCase(repository)

        val draft = save.execute(
            existing = null,
            topic = "General question",
            category = ConsultationCategory.OTHER,
            urgency = ConsultationUrgency.LOW,
            body = "",
        )

        assertNull(draft.productItemId)
        assertEquals(ConsultationDraftStatus.DRAFT, draft.status)
    }
}

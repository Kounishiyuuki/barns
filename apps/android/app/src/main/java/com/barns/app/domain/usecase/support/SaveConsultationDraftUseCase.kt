package com.barns.app.domain.usecase.support

import com.barns.app.domain.model.ConsultationCategory
import com.barns.app.domain.model.ConsultationDraft
import com.barns.app.domain.model.ConsultationDraftStatus
import com.barns.app.domain.model.ConsultationUrgency
import com.barns.app.domain.repository.ConsultationDraftRepository
import java.time.Instant
import java.util.UUID

/**
 * Saves the consultation draft locally. Local-only: the draft is never
 * sent to a server.
 */
class SaveConsultationDraftUseCase(
    private val repository: ConsultationDraftRepository,
) {
    suspend fun execute(
        existing: ConsultationDraft?,
        productItemId: String? = null,
        topic: String,
        category: ConsultationCategory,
        urgency: ConsultationUrgency,
        body: String,
    ): ConsultationDraft {
        val now = Instant.now()
        val draft = ConsultationDraft(
            id = existing?.id ?: UUID.randomUUID().toString(),
            productItemId = existing?.productItemId ?: productItemId,
            topic = topic,
            category = category,
            urgency = urgency,
            body = body,
            status = ConsultationDraftStatus.DRAFT,
            createdAt = existing?.createdAt ?: now,
            updatedAt = now,
            imageUrl = null,
        )
        repository.saveDraft(draft)
        return draft
    }
}

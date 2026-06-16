package com.barns.app.domain.usecase.support

import com.barns.app.domain.model.ConsultationDraft
import com.barns.app.domain.repository.ConsultationDraftRepository

/**
 * Returns the user's current local consultation draft, if any.
 */
class GetConsultationDraftUseCase(
    private val repository: ConsultationDraftRepository,
) {
    suspend fun execute(): ConsultationDraft? = repository.currentDraft()
}

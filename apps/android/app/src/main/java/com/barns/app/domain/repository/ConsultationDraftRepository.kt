package com.barns.app.domain.repository

import com.barns.app.domain.model.ConsultationDraft

/**
 * Local-only storage for the user's consultation draft.
 * Drafts are private and must never be sent to a server.
 */
interface ConsultationDraftRepository {
    suspend fun currentDraft(): ConsultationDraft?
    suspend fun saveDraft(draft: ConsultationDraft)
}

package com.barns.app.data.repository

import com.barns.app.domain.model.ConsultationDraft
import com.barns.app.domain.repository.ConsultationDraftRepository

/**
 * In-memory, local-only consultation draft. No persistence, no network.
 * The draft is private and is never sent to a server.
 */
class MockConsultationDraftRepository : ConsultationDraftRepository {
    private val lock = Any()
    private var draft: ConsultationDraft? = null

    override suspend fun currentDraft(): ConsultationDraft? =
        synchronized(lock) { draft }

    override suspend fun saveDraft(draft: ConsultationDraft) {
        synchronized(lock) { this.draft = draft }
    }
}

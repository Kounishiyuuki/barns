package com.barns.app.domain.model

import java.time.Instant

data class ConsultationDraft(
    val id: String,
    val productItemId: String?,
    val topic: String,
    val category: ConsultationCategory,
    val urgency: ConsultationUrgency,
    val body: String,
    val status: ConsultationDraftStatus,
    val createdAt: Instant,
    val updatedAt: Instant,
    val imageUrl: String?,
)

enum class ConsultationCategory { MAINTENANCE, CARE, REPLACEMENT, OTHER }

enum class ConsultationUrgency { LOW, NORMAL, HIGH }

enum class ConsultationDraftStatus { DRAFT, ARCHIVED }

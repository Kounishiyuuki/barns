package com.barns.app.domain.model

/**
 * Official, read-only basic information about a greenery type. Concise and
 * UI-friendly for a future basic-information screen.
 *
 * Boundary: official shared content only. No user-owned state.
 */
data class GreeneryInfo(
    val id: String,
    val name: String,
    val overview: String,
    val difficulty: String,
    val recommendedEnvironment: String,
    val lightPreference: String,
    val wateringOverview: String,
    val maintenanceNotes: String,
    val imageUrl: String?,
)
